USE monitordatabase;

CREATE TABLE sensor1_data(measurement_id INT NOT NULL AUTO_INCREMENT,
								  sensor_id varchar(45) NOT NULL,
								  value varchar(45),
								  date DATE NOT NULL,
								  time TIME NOT NULL,
								  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
								  PRIMARY KEY (measurement_id));
                          
CREATE TABLE sensor2_data(measurement_id INT NOT NULL AUTO_INCREMENT,
								  sensor_id varchar(45) NOT NULL,
								  value varchar(45),
								  date DATE NOT NULL,
								  time TIME NOT NULL,
								  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
								  PRIMARY KEY (measurement_id));   

INSERT into sensor1_data VALUES (null, "sensor1ID", 0, "2017-01-01", "00:00", CURRENT_TIMESTAMP());
INSERT into sensor2_data VALUES (null, "sensor2ID", 0, "2017-01-01", "00:00", CURRENT_TIMESTAMP());


CREATE TABLE sensor1_data_archive(measurement_id INT NOT NULL AUTO_INCREMENT,
								  sensor_id varchar(45) NOT NULL,
								  value varchar(45),
								  date DATE NOT NULL,
								  time TIME NOT NULL,
								  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
								  PRIMARY KEY (measurement_id));
                          
CREATE TABLE sensor2_data_archive(measurement_id INT NOT NULL AUTO_INCREMENT,
								  sensor_id varchar(45) NOT NULL,
								  value varchar(45),
								  date DATE NOT NULL,
								  time TIME NOT NULL,
								  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
								  PRIMARY KEY (measurement_id));   

INSERT into sensor1_data_archive VALUES (null, "sensor1ID", 0, "2017-01-01", "00:00", CURRENT_TIMESTAMP());
INSERT into sensor2_data_archive VALUES (null, "sensor2ID", 0, "2017-01-01", "00:00", CURRENT_TIMESTAMP());


CREATE TABLE heating_status(switch_num INT NOT NULL AUTO_INCREMENT,
                            switch_id INT NOT NULL,
                            heating_on BOOLEAN,
                            PRIMARY KEY (switch_num));
                            
INSERT INTO heating_status(switch_id, heating_on) VALUES ('1','0');