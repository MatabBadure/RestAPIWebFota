# /usr/bin/python
from sqlalchemy.orm import Session
from sqlalchemy import create_engine, MetaData
from sqlalchemy.ext.automap import automap_base
from datetime import datetime
# from sp_wrappers_dev import *
import cx_Oracle
import pandas as pd
import numpy as np
import pymssql
from sql import whs_sql, tims_sql
import time
# from helpers import read_query

server = 'dev'
# server = 'prod'
if server == 'prod':
    host_ip = '10.97.156.65'
else:
    host_ip = '10.97.155.34'

print 'starting'
print server


def read_query(connection, query):
    '''
    Standard function to read the results from a SQL query into a
    pandas dataframe
    '''
    cursor = connection.cursor()
    try:
        cursor.execute(query)
        names = [x[0] for x in cursor.description]
        rows = cursor.fetchall()
        return pd.DataFrame(rows, columns=names)
    finally:
        if cursor is not None:
            cursor.close()


print 'connections opened'

### assemble connections and pull data
whs_con = cx_Oracle.connect(u'whsreports/vpm123@aix49:1522/hrwhsprd')
server = 'indsdsql15v\hrtimprd'
user = 'visiview'
password = 'int3rfac#'
tims_con = pymssql.connect(server, user, password, "TIMSData")

tims_df = read_query(tims_con, tims_sql)
whs_df = read_query(whs_con, whs_sql)

### clean data
df = pd.merge(left=whs_df, right=tims_df, left_on='TIMS_CUST', right_on='tims_id')
df.columns = [x.lower() for x in df.columns]
df['vs_tr_dt'] = pd.to_datetime(df['vs_tr_dt'])
df['mo_tr_dt'] = pd.to_datetime(df['mo_tr_dt'])
df['train_dt'] = np.where(df['device_type']=='VEST', df['vs_tr_dt'], df['mo_tr_dt'])
df['g_type'] = np.where(df['device_type']=='VEST', df['garment_type'], df['monarch_type'])
df['g_size'] = np.where(df['device_type']=='VEST', df['garment_size'], df['monarch_size'])
df['g_color'] = np.where(df['device_type']=='VEST', df['garment_color'], df['monarch_color'])
df['title'] = None
df['bluetooth_id'] = None
df['middle_nm'] = None
df['lang_key'] = None
df.drop(['garment_type','garment_size','garment_color','monarch_type','monarch_size','monarch_color','tims_cust','vs_tr_dt','mo_tr_dt'], axis=1, inplace=True)
df['train_dt'] = df.train_dt.astype(object).where(df.train_dt.notnull(), None)

df = df.rename(columns={
	'g_type': 'garment_type', 
	'g_size': 'garment_size',
	'g_color': 'garment_color',
	'tims_id': 'tims_cust',
	'zip': 'zip_cd'
	})

df = df[['device_type','tims_cust','serial_num','ship_dt','hub_id','bluetooth_id','garment_cd','garment_type','garment_size','garment_color','title','first_nm','middle_nm','last_nm','email','address','zip_cd',
'primary_phone','mobile_phone','train_dt','dob','gender','lang_key','dx1','dx2','dx3','dx4']]

df.to_csv('flatfile.csv')
