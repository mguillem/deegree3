CREATE TABLE users
(
  user_id integer NOT NULL,
  username character varying(100) NOT NULL,
  password character varying(100) NOT NULL,
  enabled boolean NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (user_id )
);