update PATIENT_INFO
set first_name = 
ELT(FLOOR(1 + (RAND() * (100-1))), "James","Mary","John","Patricia","Robert","Linda","Michael","Barbara","William","Elizabeth","David","Jennifer","Richard","Maria","Charles","Susan","Joseph","Margaret","Thomas","Dorothy","Christopher","Lisa","Daniel","Nancy","Paul","Karen","Mark","Betty","Donald","Helen","George","Sandra","Kenneth","Donna","Steven","Carol","Edward","Ruth","Brian","Sharon","Ronald","Michelle","Anthony","Laura","Kevin","Sarah","Jason","Kimberly","Matthew","Deborah","Gary","Jessica","Timothy","Shirley","Jose","Cynthia","Larry","Angela","Jeffrey","Melissa","Frank","Brenda","Scott","Amy","Eric","Anna","Stephen","Rebecca","Andrew","Virginia","Raymond","Kathleen","Gregory","Pamela","Joshua","Martha","Jerry","Debra","Dennis","Amanda","Walter","Stephanie","Patrick","Carolyn","Peter","Christine","Harold","Marie","Douglas","Janet","Henry","Catherine","Carl","Frances","Arthur","Ann","Ryan","Joyce","Roger","Diane");

update PATIENT_INFO
set last_name =
ELT(FLOOR(1 + (RAND() * (100-1))), "Smith","Johnson","Williams","Jones","Brown","Davis","Miller","Wilson","Moore","Taylor","Anderson","Thomas","Jackson","White","Harris","Martin","Thompson","Garcia","Martinez","Robinson","Clark","Rodriguez","Lewis","Lee","Walker","Hall","Allen","Young","Hernandez","King","Wright","Lopez","Hill","Scott","Green","Adams","Baker","Gonzalez","Nelson","Carter","Mitchell","Perez","Roberts","Turner","Phillips","Campbell","Parker","Evans","Edwards","Collins","Stewart","Sanchez","Morris","Rogers","Reed","Cook","Morgan","Bell","Murphy","Bailey","Rivera","Cooper","Richardson","Cox","Howard","Ward","Torres","Peterson","Gray","Ramirez","James","Watson","Brooks","Kelly","Sanders","Price","Bennett","Wood","Barnes","Ross","Henderson","Coleman","Jenkins","Perry","Powell","Long","Patterson","Hughes","Flores","Washington","Butler","Simmons","Foster","Gonzales","Bryant","Alexander","Russell","Griffin","Diaz","Hayes");

update USER
set first_name = 
ELT(FLOOR(1 + (RAND() * (100-1))), "James","Mary","John","Patricia","Robert","Linda","Michael","Barbara","William","Elizabeth","David","Jennifer","Richard","Maria","Charles","Susan","Joseph","Margaret","Thomas","Dorothy","Christopher","Lisa","Daniel","Nancy","Paul","Karen","Mark","Betty","Donald","Helen","George","Sandra","Kenneth","Donna","Steven","Carol","Edward","Ruth","Brian","Sharon","Ronald","Michelle","Anthony","Laura","Kevin","Sarah","Jason","Kimberly","Matthew","Deborah","Gary","Jessica","Timothy","Shirley","Jose","Cynthia","Larry","Angela","Jeffrey","Melissa","Frank","Brenda","Scott","Amy","Eric","Anna","Stephen","Rebecca","Andrew","Virginia","Raymond","Kathleen","Gregory","Pamela","Joshua","Martha","Jerry","Debra","Dennis","Amanda","Walter","Stephanie","Patrick","Carolyn","Peter","Christine","Harold","Marie","Douglas","Janet","Henry","Catherine","Carl","Frances","Arthur","Ann","Ryan","Joyce","Roger","Diane");

update USER
set last_name =
ELT(FLOOR(1 + (RAND() * (100-1))), "Smith","Johnson","Williams","Jones","Brown","Davis","Miller","Wilson","Moore","Taylor","Anderson","Thomas","Jackson","White","Harris","Martin","Thompson","Garcia","Martinez","Robinson","Clark","Rodriguez","Lewis","Lee","Walker","Hall","Allen","Young","Hernandez","King","Wright","Lopez","Hill","Scott","Green","Adams","Baker","Gonzalez","Nelson","Carter","Mitchell","Perez","Roberts","Turner","Phillips","Campbell","Parker","Evans","Edwards","Collins","Stewart","Sanchez","Morris","Rogers","Reed","Cook","Morgan","Bell","Murphy","Bailey","Rivera","Cooper","Richardson","Cox","Howard","Ward","Torres","Peterson","Gray","Ramirez","James","Watson","Brooks","Kelly","Sanders","Price","Bennett","Wood","Barnes","Ross","Henderson","Coleman","Jenkins","Perry","Powell","Long","Patterson","Hughes","Flores","Washington","Butler","Simmons","Foster","Gonzales","Bryant","Alexander","Russell","Griffin","Diaz","Hayes");

update hillromvest_prod.PATIENT_INFO set 
email = CONCAT('test.lnt.hillrom+', first_name ,last_name,'@gmail.com');

update hillromvest_prod.PATIENT_INFO set 
address = str_random('d{1}d(2) Cc{5} [street|lane|road|park] ');

update hillromvest_prod.PATIENT_INFO set
dob = str_random_date('1920-01-01','1999-12-31','%Y-%m-%d');

-- update hillromvest_prod.PATIENT_INFO set
-- hillrom_id = str_random('d{7}');

update hillromvest_prod.PATIENT_INFO set
primary_phone = str_random('(d{3})-d{3}-d{4}');

update hillromvest_prod.PATIENT_INFO set
mobile_phone = str_random('(d{3})-d{3}-d{4}');


update hillromvest_prod.USER set 
email = CONCAT('test.lnt.hillrom+', first_name ,last_name,str_random('d{7}'),'@gmail.com');

update hillromvest_prod.USER_EXTENSION set 
address = str_random('d{1}d(2) Cc{5} [street|lane|road|park] ');

update hillromvest_prod.USER set
dob = str_random_date('1920-01-01','1999-12-31','%Y-%m-%d');


update hillromvest_prod.USER_EXTENSION set
primary_phone = str_random('(d{3})-d{3}-d{4}');

update hillromvest_prod.USER_EXTENSION set
mobile_phone = str_random('(d{3})-d{3}-d{4}');

update hillromvest_prod.CLINIC
set name = 
ELT(FLOOR(1 + (RAND() * (100-1))), "James","Mary","John","Patricia","Robert","Linda","Michael","Barbara","William","Elizabeth","David","Jennifer","Richard","Maria","Charles","Susan","Joseph","Margaret","Thomas","Dorothy","Christopher","Lisa","Daniel","Nancy","Paul","Karen","Mark","Betty","Donald","Helen","George","Sandra","Kenneth","Donna","Steven","Carol","Edward","Ruth","Brian","Sharon","Ronald","Michelle","Anthony","Laura","Kevin","Sarah","Jason","Kimberly","Matthew","Deborah","Gary","Jessica","Timothy","Shirley","Jose","Cynthia","Larry","Angela","Jeffrey","Melissa","Frank","Brenda","Scott","Amy","Eric","Anna","Stephen","Rebecca","Andrew","Virginia","Raymond","Kathleen","Gregory","Pamela","Joshua","Martha","Jerry","Debra","Dennis","Amanda","Walter","Stephanie","Patrick","Carolyn","Peter","Christine","Harold","Marie","Douglas","Janet","Henry","Catherine","Carl","Frances","Arthur","Ann","Ryan","Joyce","Roger","Diane");

update hillromvest_prod.CLINIC set 
address = str_random('d{1}d(2) Cc{5} [street|lane|road|park] ');

update hillromvest_prod.CLINIC set
phone_number = str_random('(d{3})-d{3}-d{4}');

-- update hillromvest_prod.CLINIC set
-- hillrom_id = str_random('d{7}');
