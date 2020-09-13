
CREATE TABLE IF NOT EXISTS restaurant_slot (
	id BIGINT NOT NULL, 
	slot VARCHAR(255) NOT NULL, 
	PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS restaurant_table (
	id BIGINT NOT NULL, 
	table_name VARCHAR(255) NOT NULL, 
	PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS table_slot (
	restaurant_table_id BIGINT NOT NULL, 
	restaurant_slot_id BIGINT NOT NULL, 
	PRIMARY KEY (restaurant_table_id, restaurant_slot_id));



CREATE TABLE IF NOT EXISTS table_booking (
	 id BIGINT NOT NULL, 
	 contact VARCHAR(255) NOT NULL, 
	 person_name VARCHAR(255) NOT NULL, 
	 reservation_date DATE NOT NULL, 
	 version BIGINT NOT NULL, 
	 restaurant_slot_id BIGINT NOT NULL, 
	 restaurant_table_id BIGINT NOT NULL, 
	 PRIMARY KEY (id)
 );


alter table restaurant_slot add constraint slot_index unique (slot);
alter table restaurant_table add constraint table_name_index unique (table_name);
create index table_booking_index on table_booking (reservation_date, restaurant_table_id, restaurant_slot_id);


alter table table_booking add constraint FK_booking_slot_id foreign key (restaurant_slot_id) references restaurant_slot(id);
alter table table_booking add constraint FK_booking_table_id foreign key (restaurant_table_id) references restaurant_table(id);


