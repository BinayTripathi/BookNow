create table restaurant_slot (id bigint not null, slot varchar(255) not null, primary key (id))
create table restaurant_table (id bigint not null, table_name varchar(255) not null, primary key (id))
create table table_slot (restaurant_table_id bigint not null, restaurant_slot_id bigint not null, primary key (restaurant_table_id, restaurant_slot_id))
create table table_booking (id bigint not null, contact varchar(255) not null, person_name varchar(255) not null, reservation_date date not null, version bigint not null, restaurant_slot_id bigint not null, restaurant_table_id bigint not null, primary key (id))
alter table restaurant_slot add constraint slot_index unique (slot)
alter table restaurant_table add constraint table_name_index unique (table_name)
create index table_booking_index on table_booking (reservation_date, restaurant_table_id, restaurant_slot_id)
alter table table_slot add constraint FKcwrj5oldo7lfduoqslbqpomyc foreign key (restaurant_slot_id) references restaurant_slot
alter table table_slot add constraint FK2lqoit4rl477p3g0ix4uut9qr foreign key (restaurant_table_id) references restaurant_table
alter table table_booking add constraint FKsonmpcge9l0yu7j4qvnujn8kf foreign key (restaurant_slot_id) references restaurant_slot
alter table table_booking add constraint FK8fjn0knpeiq2lfsc49hbpd7e6 foreign key (restaurant_table_id) references restaurant_table