# /usr/bin/python
from sqlalchemy.orm import Session
from sqlalchemy import create_engine, MetaData
from sqlalchemy.ext.automap import automap_base
from datetime import datetime
from sp_wrappers_dev import *
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


# assemble sqlalchemy connection info
base = automap_base()
engine = create_engine('mysql+mysqldb://root:Hillrom123@%s/hillromvest_prod' % host_ip)
metadata = MetaData()
metadata.reflect(engine, only=['JDE_LOAD_TABLE','protocol_data_temp_table','PATIENT_INFO','PATIENT_VEST_DEVICE_HISTORY',
								'PATIENT_VEST_DEVICE_HISTORY_MONARCH','PATIENT_PROTOCOL_DATA','PATIENT_PROTOCOL_DATA_MONARCH','PATIENT_DEVICES_ASSOC'])
base = automap_base(metadata=metadata)
base.prepare()
patient_info = base.classes.PATIENT_INFO
patient_device_history = base.classes.PATIENT_VEST_DEVICE_HISTORY
patient_device_history_monarch = base.classes.PATIENT_VEST_DEVICE_HISTORY_MONARCH
vest_protocol = base.classes.PATIENT_PROTOCOL_DATA
monarch_protocol = base.classes.PATIENT_PROTOCOL_DATA_MONARCH
jde_load_table = base.classes.JDE_LOAD_TABLE
protocol_data_temp_table = base.classes.protocol_data_temp_table
patient_device_assoc = base.classes.PATIENT_DEVICES_ASSOC

print 'connections opened'

class Patient:

	kind = 'patient'

	def __init__(self, hillrom_id, patient_lastname, patient_firstname, patient_zip, 
				patient_dob, patient_email, serial_number, bluetooth_id, hub_id, protocol, protocol_dt,
				training_date, dx1, dx2, dx3, dx4, garment_type, garment_size, garment_color, pat_device_type):
		self.hillrom_id = hillrom_id
		self.patient_lastname = patient_lastname
		self.patient_firstname = patient_firstname
		self.patient_zip = patient_zip
		self.patient_dob = patient_dob
		self.patient_email = patient_email
		self.serial_number = serial_number
		self.training_date = training_date
		self.dx1 = dx1
		self.dx2 = dx2
		self.dx3 = dx3
		self.dx4 = dx4
		self.garment_type = garment_type
		self.garment_size = garment_size
		self.garment_color = garment_color
		self.hub_id = hub_id
		self.bluetooth_id = bluetooth_id
		self.protocol_dt = protocol_dt
		self.protocol = protocol
		self.pat_device_type = pat_device_type
		# if hub_id is None:
		# 	self.pat_device_type = 'MONARCH'
		# else:
		# 	self.pat_device_type = 'VEST'
		if self.check_if_hillromid_exists():
			self.patient_id = self.get_existing_patientid()


### check methods

	def check_if_patient_has_device(self):
		session = Session(engine)
		rows = session.query(patient_device_assoc).filter(patient_device_assoc.hillrom_id == self.hillrom_id).filter(patient_device_assoc.is_active == 1)
		session.close()		
		if rows.count() == 0:
			return False
		else:
			return True

	def check_if_patient_has_vv(self):
		session = Session(engine)
		rows = session.query(patient_device_assoc).filter(patient_device_assoc.hillrom_id == self.hillrom_id)\
				.filter(patient_device_assoc.is_active == 1).filter(patient_device_assoc.device_type == 'VEST')
		session.close()
		if rows.count() == 0:
			return False
		else:
			return True

	def check_if_patient_has_monarch(self):
		session = Session(engine)
		rows = session.query(patient_device_assoc).filter(patient_device_assoc.hillrom_id == self.hillrom_id)\
				.filter(patient_device_assoc.is_active == 1).filter(patient_device_assoc.device_type == 'MONARCH')
		session.close()
		if rows.count() == 0:
			return False
		else:
			return True

	def check_if_hillrom_owner(self):
		session = Session(engine)
		rows = session.query(patient_info.first_name).filter(patient_info.serial_number == self.serial_number).one()
		session.close()
		if rows[0] == 'Hill-Rom':
			return True
		else:
			return False

	def check_if_hillrom_owner_monarch(self):
		session = Session(engine)
		rows = session.query(patient_info.last_name).filter(patient_info.serial_number == self.serial_number).one()
		session.close()
		if rows[0] == 'Hill-Rom':
			return True
		else:
			return False

	def check_if_hillromid_exists(self):
		session = Session(engine)
		rows = session.query(patient_info).filter(patient_info.hillrom_id == self.hillrom_id)
		session.close()
		if rows.count() == 0:
			return False
		else:
			return True

	def check_if_device_exists(self):
		session = Session(engine)
		# rows = session.query(patient_device_assoc).filter(patient_device_assoc.serial_number == self.serial_number).filter(patient_device_assoc.is_active == 1)
		# rows = session.query(patient_info).filter(patient_info.serial_number == self.serial_number)
		if self.pat_device_type != 'MONARCH':
			rows = session.query(patient_device_history).filter(patient_device_history.serial_number == self.serial_number).filter(patient_device_history.is_active == 1)
		else:
			rows = session.query(patient_device_history_monarch).filter(patient_device_history_monarch.serial_number == self.serial_number).filter(patient_device_history_monarch.is_active == 1)
		session.close()
		if rows.count() == 0:
			return False
		else:
			return True

	def check_if_pda_record_exists(self):
		session = Session(engine)
		if self.pat_device_type != 'MONARCH':
			rows = session.query(patient_device_assoc).filter(patient_device_assoc.hillrom_id == self.hillrom_id).filter(patient_device_assoc.is_active == 1).filter(patient_device_assoc.device_type == 'VEST')
		else:
			rows = session.query(patient_device_assoc).filter(patient_device_assoc.hillrom_id == self.hillrom_id).filter(patient_device_assoc.is_active == 1).filter(patient_device_assoc.device_type == 'MONARCH')
		session.close()
		if rows.count() == 0:
			return False
		else:
			return True


### data retrieval methods


	def get_existing_patientid(self):
		session = Session(engine)
		uidresult = session.query(patient_info.id).filter(patient_info.hillrom_id == self.hillrom_id).first()
		session.close()
		return uidresult[0]

	def get_existing_patientid_from_serial(self):
		session = Session(engine)
		uidresult = session.query(patient_info.id).filter(patient_info.serial_number == self.serial_number).first()
		session.close()
		return uidresult[0]

	def get_current_serial_number(self):
		session = Session(engine)
		if self.pat_device_type != 'MONARCH':
			rows = session.query(patient_device_assoc.serial_number).filter(patient_device_assoc.hillrom_id == self.hillrom_id)\
					.filter(patient_device_assoc.is_active == 1).filter(patient_device_assoc.device_type == 'VEST').first()
		else:
			rows = session.query(patient_device_assoc.serial_number).filter(patient_device_assoc.hillrom_id == self.hillrom_id)\
					.filter(patient_device_assoc.is_active == 1).filter(patient_device_assoc.device_type == 'MONARCH').first()
		session.close()
		return rows[0]


	# def get_current_device_owner_hr(self):
	# 	session = Session(engine)

	# 	if self.pat_device_type != 'MONARCH':
	# 		rows = session.query(patient_device_history).filter(patient_device_history.serial_number == self.serial_number).filter(patient_device_history.is_active == 1)
	# 	else:
	# 		rows = session.query(patient_device_history_monarch).filter(patient_device_history_monarch.serial_number == self.serial_number).filter(patient_device_history_monarch.is_active == 1)
	# 	session.close()
	# 	if rows.count() == 0:


	# 	rows = session.query(patient_device_assoc.hillrom_id).filter(patient_device_assoc.serial_number == self.serial_number).filter(patient_device_assoc.is_active == 1).one()
	# 	session.close()		
	# 	return rows[0]

	def get_current_device_owner_id(self):
		session = Session(engine)

		if self.pat_device_type != 'MONARCH':
			rows = session.query(patient_device_history.patient_id).filter(patient_device_history.serial_number == self.serial_number).filter(patient_device_history.is_active == 1)
		else:
			rows = session.query(patient_device_history_monarch.patient_id).filter(patient_device_history_monarch.serial_number == self.serial_number).filter(patient_device_history_monarch.is_active == 1)

		session.close()
		try:
			result = rows.one()
			return result[0]
		except:
			return None
		# rows = session.query(patient_device_assoc.patient_id).filter(patient_device_assoc.serial_number == self.serial_number).filter(patient_device_assoc.is_active == 1).one()
		# session.close()		
		# return rows[0]

	def get_training_date(self):
		session = Session(engine)
		if self.pat_device_type != 'MONARCH':
			rows = session.query(patient_device_assoc.training_date).filter(patient_device_assoc.hillrom_id == self.hillrom_id)\
				.filter(patient_device_assoc.is_active == 1).filter(patient_device_assoc.device_type == 'VEST')
		else:
			rows = session.query(patient_device_assoc.training_date).filter(patient_device_assoc.hillrom_id == self.hillrom_id)\
				.filter(patient_device_assoc.is_active == 1).filter(patient_device_assoc.device_type == 'MONARCH')

		session.close()
		try:
			result = rows.one()
			return result[0]
		except:
			return None

	def get_vest_protocol(self):
		session = Session(engine)
		result = session.query(
			vest_protocol.treatments_per_day,
			vest_protocol.min_minutes_per_treatment,
			vest_protocol.max_minutes_per_treatment,
			vest_protocol.min_frequency,
			vest_protocol.max_frequency,
			vest_protocol.min_pressure,
			vest_protocol.max_pressure,
			vest_protocol.last_modified_date).filter(vest_protocol.patient_id == self.patient_id).one()
		try:
			result = result.one()
			session.close()
			return dict(zip(result.keys(), result))
		except:
			session.close()
			return None

	def get_monarch_protocol(self):
		session = Session(engine)
		result = session.query(
			monarch_protocol.treatments_per_day,
			monarch_protocol.min_minutes_per_treatment,
			monarch_protocol.max_minutes_per_treatment,
			monarch_protocol.min_frequency,
			monarch_protocol.max_frequency,
			monarch_protocol.min_pressure,
			monarch_protocol.max_pressure,
			monarch_protocol.last_modified_date).filter(monarch_protocol.patient_id == self.patient_id)
		try:
			result = result.one()
			session.close()
			return dict(zip(result.keys(), result))
		except:
			session.close()
			return None	

### user management methods

	def create_patient_user(self):
		operation_type_indicator = 'CREATE'
		manage_patient_user(operation_type_indicator, self.hillrom_id, None,
                        None, None, self.patient_firstname,
                        None, self.patient_lastname, self.patient_dob,
                        self.patient_email, self.patient_zip)
		return None

	def update_patient_user(self):
		operation_type_indicator = 'UPDATE'
		manage_patient_user(operation_type_indicator, self.hillrom_id, self.hub_id,
                        self.bluetooth_id, self.serial_number, self.patient_firstname,
                        None, self.patient_lastname, self.patient_dob,
                        self.patient_email, self.patient_zip)
		return None

	def update_hillrom_user(self):
		operation_type_indicator = 'UPDATE'
		manage_patient_user(operation_type_indicator, self.hillrom_id, self.hub_id,
                self.bluetooth_id, self.serial_number, self.patient_firstname,
                None, self.patient_lastname, self.patient_dob,
                self.patient_email, self.patient_zip)
		return None

	def update_hillrom_user_monarch(self):
		operation_type_indicator = 'UPDATE'
		manage_patient_user(operation_type_indicator, self.hillrom_id, self.hub_id,
                self.bluetooth_id, self.serial_number, self.patient_firstname,
                None, self.patient_lastname, self.patient_dob,
                self.patient_email, self.patient_zip)
		return None


### patient_device_assoc methods

	def create_patient_device_assoc(self):
		operation_type_indicator = 'CREATE'
		try:
			self.patient_id = self.get_existing_patientid()
		except:
			self.patient_id = self.get_existing_patientid_from_serial()

		manage_patient_device_assoc(operation_type_indicator, self.patient_id, self.pat_device_type, None, self.serial_number,
				self.hillrom_id, None, self.training_date, self.dx1, self.dx2, self.dx3, self.dx4, self.garment_type,
				self.garment_size, self.garment_color)
		return None

	def update_patient_device_assoc(self):
		operation_type_indicator = 'UPDATE'
		try:
			self.patient_id = self.get_existing_patientid()
		except:
			self.patient_id = self.get_existing_patientid_from_serial()

		manage_patient_device_assoc(operation_type_indicator, self.patient_id, self.pat_device_type, None, self.serial_number,
				self.hillrom_id, None, self.training_date, self.dx1, self.dx2, self.dx3, self.dx4, self.garment_type,
				self.garment_size, self.garment_color)
		return None

### protocol methods

	def insert_protocol_record(self):
		session = Session(engine)
		record = protocol_data_temp_table(
			patient_id = self.patient_id,
			type = 'Normal',
			treatments_per_day = self.protocol['treatments_per_day'],
			treatment_label =  None,
			min_minutes_per_treatment =  self.protocol['min_minutes_per_treatment'],
			max_minutes_per_treatment =  self.protocol['max_minutes_per_treatment'],
			min_frequency =  self.protocol['min_frequency'],
			max_frequency =  self.protocol['max_frequency'],
			min_pressure =  self.protocol['min_pressure'],
			max_pressure =  self.protocol['max_pressure'],
			to_be_inserted =  1
			)
		session.add(record)
		session.commit()
		session.close()
		return None

	def create_protocol(self):
		if self.pat_device_type != 'MONARCH':
			create_patient_protocol('Normal', self.patient_id)
		else:
			create_patient_protocol_monarch('Normal', self.patient_id)
		pass
		
	def assemble_vest_protocol(self):

		if self.protocol == None:
			return None

		try:
			self.existing_patientid = self.get_existing_patientid()
		except:
			self.existing_patientid = self.get_existing_patientid_from_serial()

		session = Session(engine)
		rows = session.query(vest_protocol).filter(vest_protocol.patient_id == self.existing_patientid)
		session.close()
		if rows.count() == 0:
			self.insert_protocol_record()
			self.create_protocol()
		else:
			return None #protocol already exists

	def assemble_monarch_protocol(self):

		if self.protocol == None:
			return None

		try:
			self.existing_patientid = self.get_existing_patientid()
		except:
			self.existing_patientid = self.get_existing_patientid_from_serial()

		session = Session(engine)
		rows = session.query(monarch_protocol).filter(monarch_protocol.patient_id == self.existing_patientid)
		session.close()
		if rows.count() == 0:
			self.insert_protocol_record()
			self.create_protocol()
		else:
			return None #protocol already exists


### creating and updating devices

	def create_patient_device_vv(self):
		operation_type_indicator = 'CREATE'
		self.patient_id = self.get_existing_patientid()
		self.old_sn = None
		manage_patient_device(operation_type_indicator, self.patient_id, self.serial_number, self.old_sn, self.bluetooth_id, self.hub_id)
		return None

	def update_patient_device_vv(self):
		operation_type_indicator = 'UPDATE'
		self.old_sn = self.get_current_serial_number()
		manage_patient_device(operation_type_indicator, self.patient_id, self.serial_number, self.old_sn, self.bluetooth_id, self.hub_id)
		return None

	def create_patient_device_monarch(self):
		operation_type_indicator = 'CREATE'
		self.patient_id = self.get_existing_patientid()
		self.old_sn = None
		manage_patient_device_monarch(operation_type_indicator, self.patient_id, self.serial_number, self.old_sn)
		return None

	def update_patient_device_monarch(self):
		operation_type_indicator = 'UPDATE'
		self.old_sn = self.get_current_serial_number()
		manage_patient_device_monarch(operation_type_indicator, self.patient_id, self.old_sn, self.serial_number)
		return None


### inactivate devices

	def inactviate_existing_device_vv(self):
		operation_type_indicator = 'INACTIVATE'
		self.old_sn = None
		self.existing_patientid = self.get_existing_patientid()

		session = Session(engine)

		(self.existing_sn, self.existing_bt, self.existing_hub) = session.query(patient_device_history.serial_number,\
			patient_device_history.bluetooth_id, patient_device_history.hub_id)\
			.filter(patient_device_history.patient_id == self.existing_patientid).filter(patient_device_history.is_active == 1).one()

		session.close()
		manage_patient_device(operation_type_indicator, self.existing_patientid, self.existing_sn, self.old_sn, self.existing_bt, self.existing_hub)
		return None

	def inactivate_owner_vv(self):
		operation_type_indicator = 'INACTIVATE'
		self.old_sn = None
		self.owner = self.get_current_device_owner_id()
		
		session = Session(engine)
	
		(self.existing_sn, self.existing_bt, self.existing_hub) = session.query(patient_device_history.serial_number,\
			patient_device_history.bluetooth_id, patient_device_history.hub_id)\
			.filter(patient_device_history.patient_id == self.owner).filter(patient_device_history.is_active == 1).one()

		session.close()
		manage_patient_device(operation_type_indicator, self.owner, self.existing_sn, self.old_sn, self.existing_bt, self.existing_hub)
		return None

	def inactivate_patient_device_monarch(self):
		operation_type_indicator = 'INACTIVATE'
		self.old_sn = None
		self.existing_patientid = self.get_existing_patientid()
		session = Session(engine)

		(self.existing_sn, self.existing_bt, self.existing_hub) = session.query(patient_device_history_monarch.serial_number,\
			patient_device_history_monarch.bluetooth_id, patient_device_history_monarch.hub_id)\
			.filter(patient_device_history_monarch.patient_id == self.existing_patientid).filter(patient_device_history_monarch.is_active == 1).one()

		session.close()
		manage_patient_device_monarch(operation_type_indicator, self.existing_patientid, self.existing_sn, self.old_sn)
		return None

	def inactivate_owner_monarch(self):
		operation_type_indicator = 'INACTIVATE'
		self.old_sn = None
		self.owner = self.get_current_device_owner_id()

		session = Session(engine)
		
		(self.existing_sn, self.existing_bt, self.existing_hub) = session.query(patient_device_history_monarch.serial_number,\
			patient_device_history_monarch.bluetooth_id, patient_device_history_monarch.hub_id)\
			.filter(patient_device_history_monarch.patient_id == self.owner).filter(patient_device_history_monarch.is_active == 1).one()

		session.close()
		manage_patient_device_monarch(operation_type_indicator, self.owner, self.existing_sn, self.old_sn) #, self.existing_bt, self.existing_hub)
		return None


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
df['email'] = None
df['address'] = None
df['primary_phone'] = None
df['mobile_phone'] = None
df['gender'] = None
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