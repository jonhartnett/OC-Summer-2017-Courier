DROP TABLE IF EXISTS Invoice_Tickets;
DROP TABLE IF EXISTS Ticket;
DROP TABLE IF EXISTS Invoice;
DROP TABLE IF EXISTS Driver;
DROP TABLE IF EXISTS Client;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS SystemInfo;

CREATE TABLE System_Info
(
  id    INT AUTO_INCREMENT PRIMARY KEY,
  speed FLOAT NOT NULL,
  base  DECIMAL NOT NULL,
  price DECIMAL NOT NULL,
  bonus DECIMAL NOT NULL,

  CONSTRAINT System_id_uindex UNIQUE (id)
);


CREATE TABLE User
(
  id       INT AUTO_INCREMENT
    PRIMARY KEY,
  username VARCHAR(128) NOT NULL,
  name     VARCHAR(256) NOT NULL,
  password BINARY(64)   NOT NULL,
  salt     CHAR(16)     NOT NULL,
  type     VARCHAR(32)  NOT NULL,
  CONSTRAINT User_id_uindex
  UNIQUE (id),
  CONSTRAINT User_username_uindex
  UNIQUE (username)
);

CREATE TABLE Client
(
  id                    INT AUTO_INCREMENT PRIMARY KEY,
  name                  VARCHAR(256)  NOT NULL,
  address               VARCHAR(256)  NOT NULL,
  delivery_instructions VARCHAR(2048) NULL,

  CONSTRAINT Client_id_uindex UNIQUE (id)
);

CREATE TABLE Driver
(
  id   INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(256) NOT NULL,

  CONSTRAINT Driver_id_uindex UNIQUE (id)
);

CREATE TABLE Invoice
(
  id          INT AUTO_INCREMENT PRIMARY KEY,
  description VARCHAR(256) NULL,
  client_id   INT          NOT NULL,
  CONSTRAINT Invoice_id_uindex
  UNIQUE (id),
  CONSTRAINT Invoice_Client_id_fk
  FOREIGN KEY (client_id) REFERENCES `oc-summer-2017-courier`.Client (id)
    ON UPDATE CASCADE
);

CREATE TABLE Ticket
(
  id                    INT AUTO_INCREMENT
    PRIMARY KEY,
  description           VARCHAR(256) NULL,
  date                  DATETIME     NULL,
  order_taker_id        INT          NULL,
  package_number        INT          NULL,
  est_delivery_time     DATETIME     NULL,
  est_distance          DOUBLE       NULL,
  driver_id             INT          NULL,
  assigned_leave_time   DATETIME     NULL,
  pickup_time           DATETIME     NULL,
  delivery_time         DATETIME     NULL,
  pickup_client_id      INT          NULL,
  delivery_client_id    INT          NULL,
  charge_to_destination TINYINT(1)   NULL,
  price                 DECIMAL      NULL,
  CONSTRAINT Ticket_id_uindex
  UNIQUE (id),
  CONSTRAINT Ticket_Driver_id_fk
  FOREIGN KEY (driver_id) REFERENCES `oc-summer-2017-courier`.Driver (id)
    ON UPDATE CASCADE,
  CONSTRAINT Ticket_Pickup_client_id_fk
  FOREIGN KEY (pickup_client_id) REFERENCES `oc-summer-2017-courier`.Client (id)
    ON UPDATE CASCADE,
  CONSTRAINT Ticket_Delivery_client_id_fk
  FOREIGN KEY (delivery_client_id) REFERENCES `oc-summer-2017-courier`.Client (id)
    ON UPDATE CASCADE
);

CREATE TABLE Invoice_Tickets
(
  Invoice_id INT NOT NULL,
  Ticket_id  INT NOT NULL,
  PRIMARY KEY (Ticket_id, Invoice_id),
  CONSTRAINT Invoice_Ticket_Invoice_id_fk
  FOREIGN KEY (Invoice_id) REFERENCES `oc-summer-2017-courier`.Invoice (id)
    ON UPDATE CASCADE
);

CREATE INDEX Invoice_Client_id_fk
  ON Invoice (client_id);

CREATE INDEX Invoice_Ticket_Invoice_id_fk
  ON Invoice_Tickets (Invoice_id);

CREATE INDEX Ticket_Delivery_client_id_fk
  ON Ticket (delivery_client_id);

CREATE INDEX Ticket_Driver_id_fk
  ON Ticket (driver_id);

CREATE INDEX Ticket_Order_taker_id_fk
  ON Ticket (order_taker_id);

CREATE INDEX Ticket_Pickup_client_id_fk
  ON Ticket (pickup_client_id);

ALTER TABLE Invoice_Tickets
  ADD CONSTRAINT Invoice_Ticket_Ticket_id_fk
FOREIGN KEY (Ticket_id) REFERENCES `oc-summer-2017-courier`.Ticket (id)
  ON UPDATE CASCADE;

ALTER TABLE Ticket
  ADD CONSTRAINT Ticket_Order_taker_id_fk
FOREIGN KEY (order_taker_id) REFERENCES `oc-summer-2017-courier`.User (id)
  ON UPDATE CASCADE;
