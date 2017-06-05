create table Client
(
	id int auto_increment
		primary key,
	name varchar(256) not null,
	address varchar(256) not null,
	delivery_instructions varchar(2048) null,
	constraint Client_id_uindex
	unique (id)
)
;

create table Driver
(
	id int auto_increment
		primary key,
	name varchar(256) not null,
	constraint Driver_id_uindex
	unique (id)
)
;

create table Invoice
(
	id int auto_increment
		primary key,
	description varchar(256) null,
	client_id int not null,
	constraint Invoice_id_uindex
	unique (id),
	constraint Invoice_Client_id_fk
	foreign key (client_id) references `oc-summer-2017-courier`.Client (id)
		on update cascade
)
;

create index Invoice_Client_id_fk
	on Invoice (client_id)
;

create table Invoice_Tickets
(
	Invoice_id int not null,
	Ticket_id int not null,
	primary key (Ticket_id, Invoice_id),
	constraint Invoice_Ticket_Invoice_id_fk
	foreign key (Invoice_id) references `oc-summer-2017-courier`.Invoice (id)
		on update cascade
)
;

create index Invoice_Ticket_Invoice_id_fk
	on Invoice_Tickets (Invoice_id)
;

create table Ticket
(
	id int auto_increment
		primary key,
	description varchar(256) null,
	date datetime null,
	order_taker_id int null,
	package_number int null,
	est_delivery_time datetime null,
	est_distance double null,
	driver_id int null,
	assigned_leave_time datetime null,
	pickup_time datetime null,
	delivery_time datetime null,
	pickup_client_id int null,
	delivery_client_id int null,
	charge_to_destination tinyint(1) null,
	price decimal null,
	constraint Ticket_id_uindex
	unique (id),
	constraint Ticket_Driver_id_fk
	foreign key (driver_id) references `oc-summer-2017-courier`.Driver (id)
		on update cascade,
	constraint Ticket_Pickup_client_id_fk
	foreign key (pickup_client_id) references `oc-summer-2017-courier`.Client (id)
		on update cascade,
	constraint Ticket_Delivery_client_id_fk
	foreign key (delivery_client_id) references `oc-summer-2017-courier`.Client (id)
		on update cascade
)
;

create index Ticket_Delivery_client_id_fk
	on Ticket (delivery_client_id)
;

create index Ticket_Driver_id_fk
	on Ticket (driver_id)
;

create index Ticket_Order_taker_id_fk
	on Ticket (order_taker_id)
;

create index Ticket_Pickup_client_id_fk
	on Ticket (pickup_client_id)
;

alter table Invoice_Tickets
	add constraint Invoice_Ticket_Ticket_id_fk
foreign key (Ticket_id) references `oc-summer-2017-courier`.Ticket (id)
	on update cascade
;

create table User
(
	id int auto_increment
		primary key,
	username varchar(128) not null,
	name varchar(256) not null,
	password binary(64) not null,
	salt char(16) not null,
	type varchar(32) not null,
	constraint User_id_uindex
	unique (id),
	constraint User_username_uindex
	unique (username)
)
;

alter table Ticket
	add constraint Ticket_Order_taker_id_fk
foreign key (order_taker_id) references `oc-summer-2017-courier`.User (id)
	on update cascade
;
