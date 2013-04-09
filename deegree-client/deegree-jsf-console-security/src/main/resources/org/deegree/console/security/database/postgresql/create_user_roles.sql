CREATE TABLE user_roles
(
  user_role_id integer NOT NULL,
  user_id integer NOT NULL,
  authority character varying(100) NOT NULL,
  CONSTRAINT user_roles_pkey PRIMARY KEY (user_role_id ),
  CONSTRAINT fk_user_roles FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);