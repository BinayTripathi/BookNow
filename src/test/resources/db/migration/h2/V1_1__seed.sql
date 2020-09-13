INSERT INTO Restaurant_Table(id,table_name) VALUES (1,'table1');
INSERT INTO Restaurant_Table(id,table_name) VALUES (2,'table2');
INSERT INTO Restaurant_Table(id,table_name) VALUES (3,'table3');
INSERT INTO Restaurant_Table(id,table_name) VALUES (4,'table4');
INSERT INTO Restaurant_Table(id,table_name) VALUES (5,'table5');
INSERT INTO Restaurant_Table(id,table_name) VALUES (6,'table6');
INSERT INTO Restaurant_Table(id,table_name) VALUES (7,'table7');
INSERT INTO Restaurant_Table(id,table_name) VALUES (8,'table8');
INSERT INTO Restaurant_Table(id,table_name) VALUES (9,'table9');
INSERT INTO Restaurant_Table(id,table_name) VALUES (10,'table10');
INSERT INTO Restaurant_Table(id,table_name) VALUES (11,'table11');
INSERT INTO Restaurant_Table(id,table_name) VALUES (12,'table12');
INSERT INTO Restaurant_Table(id,table_name) VALUES (13,'table13');
INSERT INTO Restaurant_Table(id,table_name) VALUES (14,'table14');
INSERT INTO Restaurant_Table(id,table_name) VALUES (15,'table15');
INSERT INTO Restaurant_Table(id,table_name) VALUES (16,'table16');
INSERT INTO Restaurant_Table(id,table_name) VALUES (17,'table17');
INSERT INTO Restaurant_Table(id,table_name) VALUES (18,'table18');
INSERT INTO Restaurant_Table(id,table_name) VALUES (19,'table19');
INSERT INTO Restaurant_Table(id,table_name) VALUES (20,'table20');


INSERT INTO Restaurant_Slot(id,slot) VALUES (10001,'11AM-1PM');
INSERT INTO Restaurant_Slot(id,slot) VALUES (10002,'1PM-3PM');
INSERT INTO Restaurant_Slot(id,slot) VALUES (10003,'3PM-5PM');
INSERT INTO Restaurant_Slot(id,slot) VALUES (10004,'5PM-7PM');

INSERT INTO Table_slot (
Select t.id , s.id from restaurant_table t, restaurant_slot s
)

