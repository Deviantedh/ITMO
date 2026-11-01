CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(90) UNIQUE,
                       hashedPassword VARCHAR(90)
);

CREATE TABLE vehicles (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(80),
                          x FLOAT,
                          y FLOAT,
                          creationDate TIMESTAMP,
                          xlf FLOAT,
                          ylf INTEGER,
                          zlf FLOAT,
                          namef VARCHAR(90),
                          xlt FLOAT,
                          ylt INTEGER,
                          zlt FLOAT,
                          namet VARCHAR(90),
                          distance INTEGER,
                          creatorID INT REFERENCES users(id)
);