whs_sql = """
select y.device_type
      ,y.tims_cust
      ,y.serial_num
      ,y.ship_dt
      ,y.hub_id
      ,g.garment_cd
      ,g.garment_type
      ,g.garment_size
      ,g.garment_color
      ,m.monarch_type
      ,m.monarch_size
      ,m.monarch_color
  from ( select x.*, rank ( ) over ( partition by tims_id_raw, device_type order by ship_dt desc ) rn
           from ( SELECT 'MONARCH' device_type
                          ,cso.hdr_usr_rsrvd_num tims_id_raw
                          ,CASE
                              WHEN cso.hdr_usr_rsrvd_num <= 9 THEN '0000' || cso.hdr_usr_rsrvd_num
                              WHEN cso.hdr_usr_rsrvd_num <= 99 THEN '000' || cso.hdr_usr_rsrvd_num
                              WHEN cso.hdr_usr_rsrvd_num <= 999 THEN '00' || cso.hdr_usr_rsrvd_num
                              WHEN cso.hdr_usr_rsrvd_num <= 9999 THEN '0' || cso.hdr_usr_rsrvd_num
                              WHEN cso.hdr_usr_rsrvd_num > 99999 THEN CHR ( SUBSTR ( cso.hdr_usr_rsrvd_num, 1, 2 ) ) || SUBSTR ( cso.hdr_usr_rsrvd_num, 3, 4 )
                              ELSE TO_CHAR ( cso.hdr_usr_rsrvd_num )
                           END
                              tims_cust
                          ,cso.hdr_usr_rsrvd_amt tims_order
                          ,sn.serial_num1 serial_num
                          ,cso.dtl_actual_ship_dt ship_dt
                          ,cso.dtl_item_cd item
                          ,cso.dtl_order_type order_type
                          ,cso.dtl_order_num order_num
                          ,null hub_id
                      FROM ods.ods_capital_sales_order cso
                          ,ods.ods_serial_number sn
                     WHERE     cso.fk_ship_to_customer = 1026846
                           AND sn.fk_capital_sales_order = cso.pk_capital_sales_order
                           AND cso.dtl_item_cd IN ('PMACS1LT', 'PMACS1LTA', 'PMACS1WARL', 'PMACS1WARW', 'PMACS1WI'
                                                  ,'PMACS1WIA')
                           AND cso.dtl_order_type IN ('SR', 'SW')
                  union
                  select 'VEST' device_type
                        ,cso.hdr_usr_rsrvd_num
                        ,case
                            when cso.hdr_usr_rsrvd_num <= 9 then '0000' || cso.hdr_usr_rsrvd_num
                            when cso.hdr_usr_rsrvd_num <= 99 then '000' || cso.hdr_usr_rsrvd_num
                            when cso.hdr_usr_rsrvd_num <= 999 then '00' || cso.hdr_usr_rsrvd_num
                            when cso.hdr_usr_rsrvd_num <= 9999 then '0' || cso.hdr_usr_rsrvd_num
                            when cso.hdr_usr_rsrvd_num > 99999 then chr ( substr ( cso.hdr_usr_rsrvd_num, 1, 2 ) ) || substr ( cso.hdr_usr_rsrvd_num, 3, 4 )
                            else to_char ( cso.hdr_usr_rsrvd_num )
                         end
                            tims_cust
                        ,cso.hdr_usr_rsrvd_amt
                        ,substr(SN.SERIAL_NUM1,1,8) SERIAL_NUM1
                        ,cso.dtl_actual_ship_dt
                        ,cso.dtl_item_cd
                        ,cso.dtl_order_type
                        ,cso.dtl_order_num
                        , ( select case when sni.serial_num1 like '%QUAL%' then sni.serial_num1 else null end sn
                              from ods.ods_capital_sales_order csoi
                                  ,ods.ods_serial_number sni
                             where csoi.dtl_order_num = cso.dtl_order_num and sni.fk_capital_sales_order = csoi.pk_capital_sales_order and csoi.dtl_item_cd = '192194' )
                            hub_id
                    from ods.ods_capital_sales_order cso
                        ,ods.ods_serial_number sn
                   where     cso.fk_ship_to_customer = 1026846
                         and sn.fk_capital_sales_order = cso.pk_capital_sales_order
                         and cso.dtl_item_cd like '%BT%'
                         and cso.dtl_order_type in ('SR', 'SW')
                                --and cso.hdr_usr_rsrvd_num in (659481, 659570, 659789, 659999, 39047
                                    --,659746, 659789, 660273, 660293, 659748
                                  --  ,659525, 659699, 660678, 658205, 661203
                                    --,659692)
                         and cso.dtl_order_num in (select cso.dtl_order_num
                                                     from ods.ods_capital_sales_order cso
                                                    where cso.dtl_item_cd = '192194') ) x ) y
      , ( select *
            from ( select cso.hdr_usr_rsrvd_num tims_id_raw
                         ,cso.dtl_item_cd garment_cd
                         ,case
                             when cso.dtl_item_cd in ('P300645000', 'P300644000', 'P300640000', 'P300643000', 'P300642000'
                                                     ,'P300641000') then
                                'Wrap'
                             else
                                'C3 Washable'
                          end
                             garment_type
                         ,case
                             when cso.dtl_item_cd = '500310000' then 'Child Small'
                             when cso.dtl_item_cd = '500370000' then 'Adult Large'
                             when cso.dtl_item_cd = '500370001' then 'Adult Large'
                             when cso.dtl_item_cd = '500370002' then 'Adult Large'
                             when cso.dtl_item_cd = '500370003' then 'Adult Large'
                             when cso.dtl_item_cd = '500370004' then 'Adult Large'
                             when cso.dtl_item_cd = '500360001' then 'Adult Medium'
                             when cso.dtl_item_cd = '500360002' then 'Adult Medium'
                             when cso.dtl_item_cd = '500360003' then 'Adult Medium'
                             when cso.dtl_item_cd = '500360004' then 'Adult Medium'
                             when cso.dtl_item_cd = '500360000' then 'Adult Medium'
                             when cso.dtl_item_cd = '500350003' then 'Adult Small'
                             when cso.dtl_item_cd = '500350000' then 'Adult Small'
                             when cso.dtl_item_cd = '500350001' then 'Adult Small'
                             when cso.dtl_item_cd = '500350002' then 'Adult Small'
                             when cso.dtl_item_cd = '500340000' then 'Adult X-Small'
                             when cso.dtl_item_cd = '500340001' then 'Adult X-Small'
                             when cso.dtl_item_cd = '500340003' then 'Adult X-Small'
                             when cso.dtl_item_cd = '500330000' then 'Child Large'
                             when cso.dtl_item_cd = '500330001' then 'Child Large'
                             when cso.dtl_item_cd = '500330002' then 'Child Large'
                             when cso.dtl_item_cd = '500330003' then 'Child Large'
                             when cso.dtl_item_cd = '500320001' then 'Child Medium'
                             when cso.dtl_item_cd = '500320002' then 'Child Medium'
                             when cso.dtl_item_cd = '500320003' then 'Child Medium'
                             when cso.dtl_item_cd = '500320000' then 'Child Medium'
                             when cso.dtl_item_cd = '500310001' then 'Child Small'
                             when cso.dtl_item_cd = '500310002' then 'Child Small'
                             when cso.dtl_item_cd = '500310003' then 'Child Small'
                             when cso.dtl_item_cd = '500370006' then 'Adult Large'
                             when cso.dtl_item_cd = '500360006' then 'Adult Medium'
                             when cso.dtl_item_cd = '500350006' then 'Adult Small'
                             when cso.dtl_item_cd = '500340005' then 'Adult X-Small'
                             when cso.dtl_item_cd = '500340004' then 'Adult X-Small'
                             when cso.dtl_item_cd = '500340006' then 'Adult X-Small'
                             when cso.dtl_item_cd = '500370005' then 'Adult Large'
                             when cso.dtl_item_cd = '500360005' then 'Adult Medium'
                             when cso.dtl_item_cd = '500350005' then 'Adult Small'
                             when cso.dtl_item_cd = '500350004' then 'Adult Small'
                             when cso.dtl_item_cd = '500300000' then 'Child X-Small'
                             when cso.dtl_item_cd = '500330005' then 'Child Large'
                             when cso.dtl_item_cd = '500330004' then 'Child Large'
                             when cso.dtl_item_cd = '500330006' then 'Child Large'
                             when cso.dtl_item_cd = '500320005' then 'Child Medium'
                             when cso.dtl_item_cd = '500320006' then 'Child Medium'
                             when cso.dtl_item_cd = '500320004' then 'Child Medium'
                             when cso.dtl_item_cd = '500310005' then 'Child Small'
                             when cso.dtl_item_cd = '500310004' then 'Child Small'
                             when cso.dtl_item_cd = '500310006' then 'Child Small'
                             when cso.dtl_item_cd = '500300002' then 'Child X-Small'
                             when cso.dtl_item_cd = '500300003' then 'Child X-Small'
                             when cso.dtl_item_cd = 'P300645000' then '2X-Large'
                             when cso.dtl_item_cd = 'P300644000' then 'X-Large'
                             when cso.dtl_item_cd = 'P300640000' then 'X-Small'
                             when cso.dtl_item_cd = 'P300643000' then 'Large'
                             when cso.dtl_item_cd = 'P300642000' then 'Medium'
                             when cso.dtl_item_cd = 'P300641000' then 'Small'
                             else null
                          end
                             garment_size
                         ,case
                             when cso.dtl_item_cd = '500310000' then null
                             when cso.dtl_item_cd = '500370000' then null
                             when cso.dtl_item_cd = '500370001' then 'Calming Black'
                             when cso.dtl_item_cd = '500370002' then 'Coastal Blue'
                             when cso.dtl_item_cd = '500370003' then 'Cashmere Rose'
                             when cso.dtl_item_cd = '500370004' then 'Color Me Purple'
                             when cso.dtl_item_cd = '500360001' then 'Calming Black'
                             when cso.dtl_item_cd = '500360002' then 'Coastal Blue'
                             when cso.dtl_item_cd = '500360003' then 'Cashmere Rose'
                             when cso.dtl_item_cd = '500360004' then 'Color Me Purple'
                             when cso.dtl_item_cd = '500360000' then null
                             when cso.dtl_item_cd = '500350003' then 'Cashmere Rose'
                             when cso.dtl_item_cd = '500350000' then null
                             when cso.dtl_item_cd = '500350001' then 'Calming Black'
                             when cso.dtl_item_cd = '500350002' then 'Coastal Blue'
                             when cso.dtl_item_cd = '500340000' then null
                             when cso.dtl_item_cd = '500340001' then 'Coastal Blue'
                             when cso.dtl_item_cd = '500340003' then 'Cashmere Rose'
                             when cso.dtl_item_cd = '500330000' then null
                             when cso.dtl_item_cd = '500330001' then 'Calming Black'
                             when cso.dtl_item_cd = '500330002' then 'Coastal Blue'
                             when cso.dtl_item_cd = '500330003' then 'Cashmere Rose'
                             when cso.dtl_item_cd = '500320001' then 'Calming Black'
                             when cso.dtl_item_cd = '500320002' then 'Coastal Blue'
                             when cso.dtl_item_cd = '500320003' then 'Cashmere Rose'
                             when cso.dtl_item_cd = '500320000' then null
                             when cso.dtl_item_cd = '500310001' then 'Calming Black'
                             when cso.dtl_item_cd = '500310002' then 'Coastal Blue'
                             when cso.dtl_item_cd = '500310003' then 'Cashmere Rose'
                             when cso.dtl_item_cd = '500370006' then 'Pink Camo'
                             when cso.dtl_item_cd = '500360006' then 'Pink Camo'
                             when cso.dtl_item_cd = '500350006' then 'Pink Camo'
                             when cso.dtl_item_cd = '500340005' then 'Cool Camo'
                             when cso.dtl_item_cd = '500340004' then 'Color Me Purple'
                             when cso.dtl_item_cd = '500340006' then 'Pink Camo'
                             when cso.dtl_item_cd = '500370005' then 'Cool Camo'
                             when cso.dtl_item_cd = '500360005' then 'Cool Camo'
                             when cso.dtl_item_cd = '500350005' then 'Cool Camo'
                             when cso.dtl_item_cd = '500350004' then 'Color Me Purple'
                             when cso.dtl_item_cd = '500300000' then null
                             when cso.dtl_item_cd = '500330005' then 'Cool Camo'
                             when cso.dtl_item_cd = '500330004' then 'Color Me Purple'
                             when cso.dtl_item_cd = '500330006' then 'Pink Camo'
                             when cso.dtl_item_cd = '500320005' then 'Cool Camo'
                             when cso.dtl_item_cd = '500320006' then 'Pink Camo'
                             when cso.dtl_item_cd = '500320004' then 'Color Me Purple'
                             when cso.dtl_item_cd = '500310005' then 'Cool Camo'
                             when cso.dtl_item_cd = '500310004' then 'Color Me Purple'
                             when cso.dtl_item_cd = '500310006' then 'Pink Camo'
                             when cso.dtl_item_cd = '500300002' then 'Coastal Blue'
                             when cso.dtl_item_cd = '500300003' then 'Cashmere Rose'
                             else null
                          end
                             garment_color
                         ,rank ( ) over ( partition by cso.hdr_usr_rsrvd_num order by cso.dtl_actual_ship_dt desc, cso.pk_capital_sales_order desc ) rn
                     from ods.ods_capital_sales_order cso
                    where     cso.fk_ship_to_customer = 1026846
                          and cso.dtl_item_cd in ('500310000', '500370000', '500370001', '500370002', '500370003'
                                                 ,'500370004', '500360001', '500360002', '500360003', '500360004'
                                                 ,'500360000', '500350003', '500350000', '500350001', '500350002'
                                                 ,'500340000', '500340001', '500340003', '500330000', '500330001'
                                                 ,'500330002', '500330003', '500320001', '500320002', '500320003'
                                                 ,'500320000', '500310001', '500310002', '500310003', '500370006'
                                                 ,'500360006', '500350006', '500340005', '500340004', '500340006'
                                                 ,'500370005', '500360005', '500350005', '500350004', '500300000'
                                                 ,'500330005', '500330004', '500330006', '500320005', '500320006'
                                                 ,'500320004', '500310005', '500310004', '500310006', '500300002'
                                                 ,'500300003', 'P300645000', 'P300644000', 'P300640000', 'P300643000'
                                                 ,'P300642000', 'P300641000') )
           where rn = 1 ) g
      , ( select *
            from ( select cso.hdr_usr_rsrvd_num tims_id_raw
                         ,cso.dtl_item_cd monarch_cd
                         ,'Monarch Shell' monarch_type
                         ,'One Size Fits All' monarch_size
                         ,case cso.dtl_item_cd
                             when '198686' then 'Zesty Pink'
                             when '198687' then 'Techno Red'
                             when '198684' then 'Ocean Blue'
                             when '198685' then 'Adventure Green'
                             when '198683' then 'Graphite Orange'
                             else null
                          end
                             monarch_color
                         ,dense_rank ( ) over ( partition by cso.hdr_usr_rsrvd_num order by cso.dtl_actual_ship_dt desc, cso.pk_capital_sales_order desc ) rn
                     from ods.ods_capital_sales_order cso
                    where cso.fk_ship_to_customer = 1026846 and cso.dtl_item_cd in ('198686', '198687', '198684', '198685', '198683') )
           where rn = 1 ) m
 where     y.rn = 1
       and y.tims_id_raw = g.tims_id_raw(+)
       and y.tims_id_raw = m.tims_id_raw(+)
       and y.ship_dt >= trunc(sysdate) - 7
order by y.ship_dt ASC  
"""

tims_sql = """
set transaction isolation level read uncommitted

select
  tims_id,
  first_nm,
  last_nm,
  email,
  address,
  zip,
  primary_phone,
  mobile_phone,
  vs_tr_dt,
  mo_tr_dt,
  dob,
  gender,
  dx1,
  dx2,
  dx3,
  dx4
from (select
  arc.customercode tims_id,
  upper(left(utp.firstname, 1)) + lower(substring(utp.firstname, 2, len(utp.firstname))) first_nm,
  upper(left(utp.lastname, 1)) + lower(substring(utp.lastname, 2, len(utp.lastname))) last_nm,
  utp.PrimaryEmailAddress email,
  uta.AddressLine1 address,
  left(uta.zip, 5) zip,
  utp.PrimaryPhoneNumber primary_phone, 
  utp.SecondaryPhoneNumber mobile_phone,
  v.vs_tr_dt,
  m.mo_tr_dt,
  pt.Birthdate dob,
  CASE pt.GenderId
    WHEN 1 THEN 'F'
    WHEN 2 THEN 'M'
    WHEN 3 THEN NULL
  END gender,
  dx.*,
  rank() over (partition by arc.arcustomerid order by
  isnull(v.vs_tr_dt, '2079-06-05T23:59:00') asc,
  isnull(m.mo_tr_dt, '2079-06-05T23:59:00') asc) rn
from arcustomer arc
join utperson utp
  on utp.utpersonid = arc.personid
join MBPatient pt on pt.UTPersonId = utp.UTPersonId
join utaddress uta
  on uta.utaddressid = arc.addressid
left join (select
  vet.hfcwotrainingdate vs_tr_dt,
  cmn.rowid
from [eisplusvtraining vest notes] vet
join cusmasnotes cmn
  on vet.noteid = cmn.noteid) v
  on arc.arcustomerid = v.rowid
left join (select
  mot.hfcwotrainingdate mo_tr_dt,
  cmn.rowid
from [eisplusvtraining monarch notes] mot
join cusmasnotes cmn
  on mot.noteid = cmn.noteid) m
  on arc.arcustomerid = m.rowid
cross apply (select top 1
  pth.icd10diagnosiscode1 dx1,
  pth.icd10diagnosiscode2 dx2,
  pth.icd10diagnosiscode3 dx3,
  pth.icd10diagnosiscode4 dx4
from patthp pth
where pth.cusno = arc.customercode
order by pth.therapy_seq desc) dx
) x
where x.rn = 1
"""
