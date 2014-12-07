--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: product_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE product_type AS ENUM (
    'unknown',
    'project',
    'module',
    'widget',
    'file'
);


ALTER TYPE public.product_type OWNER TO postgres;

--
-- Name: company_delete(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION company_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
	i project;
	j product;
BEGIN
	-- Remove refrence between employee and 'company-director'
	DELETE FROM employee_group WHERE employee_id = OLD.director_id AND group_id = (
		SELECT id FROM groups WHERE name = 'company-director'
	);
	-- Create reference between employee and 'employee'
	DELETE FROM employee_group WHERE employee_id = OLD.director_id AND group_id = (
		SELECT id FROM groups WHERE name = 'employee'
	);
	-- Delete all dependencies
	FOR i IN SELECT pj.* FROM project AS pj JOIN product AS pr ON pj.product_id = pr.id WHERE pr.company_id = OLD.id LOOP
		DELETE FROM project WHERE id = pj.id;
	END LOOP;
	FOR j IN SELECT pr.* FROM project AS pj JOIN product AS pr ON pj.product_id = pr.id WHERE pr.company_id = OLD.id LOOP
		DELETE FROM product WHERE id = pr.id;
	END LOOP;
	-- Delete all employees
	DELETE FROM employee WHERE company_id = OLD.id;
	RETURN OLD;
END;
$$;


ALTER FUNCTION public.company_delete() OWNER TO postgres;

--
-- Name: company_insert(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION company_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	-- Skip if director's id is NULL
	IF NEW.director_id = 0 THEN
		RETURN NEW;
	END IF;
	-- Create reference between employee and 'company-director'
	INSERT INTO employee_group (employee_id, group_id) VALUES (NEW.director_id, (
		SELECT id FROM groups WHERE name = 'company-director'
	));
	-- Create reference between employee and 'employee'
	INSERT INTO employee_group (employee_id, group_id) VALUES (NEW.director_id, (
		SELECT id FROM groups WHERE name = 'employee'
	));
	RETURN NEW;
END;
$$;


ALTER FUNCTION public.company_insert() OWNER TO postgres;

--
-- Name: privilege_insert(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION privilege_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	INSERT INTO group_privilege (group_id, privilege_id) VALUES (
		(SELECT id FROM groups WHERE name = 'god'), NEW.id
	);
	RETURN NEW;
END;
$$;


ALTER FUNCTION public.privilege_insert() OWNER TO postgres;

--
-- Name: product_delete(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION product_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	-- Delete reference between creator and 'product-creator' group
	DELETE FROM employee_group WHERE employee_id = OLD.creator_id AND group_id = (
		SELECT id FROM groups WHERE name = 'product-creator'
	);
	-- Delete reference between creator and 'employee' group
	DELETE FROM employee_group WHERE employee_id = OLD.creator_id AND group_id = (
		SELECT id FROM groups WHERE name = 'employee'
	);
	-- Delete connector between product and it's employee
	DELETE FROM product_employee WHERE product_id = OLD.id;
	-- Remove all product's children
	DELETE FROM product WHERE parent_id = OLD.id AND id <> OLD.id;
	RETURN OLD;
END;
$$;


ALTER FUNCTION public.product_delete() OWNER TO postgres;

--
-- Name: product_insert(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION product_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	-- Create reference between creator and 'product-creator' group
	INSERT INTO employee_group (employee_id, group_id) VALUES (NEW.creator_id, (
		SELECT id FROM groups WHERE name = 'product-creator'
	));
	-- Create reference between creator and 'employee' group
	INSERT INTO employee_group (employee_id, group_id) VALUES (NEW.creator_id, (
		SELECT id FROM groups WHERE name = 'employee'
	));
	-- Create reference between product and it's creator
	INSERT INTO product_employee (product_id, employee_id) VALUES (NEW.id, NEW.creator_id);
	RETURN NEW;
END;
$$;


ALTER FUNCTION public.product_insert() OWNER TO postgres;

--
-- Name: project_delete(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION project_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	-- Remove reference between leader and 'project-leader' group;
	DELETE FROM employee_group WHERE employee_id = OLD.leader_id AND group_id = (
		SELECT id FROM groups WHERE name = 'project-leader'
	);
	RETURN OLD;
END;
$$;


ALTER FUNCTION public.project_delete() OWNER TO postgres;

--
-- Name: project_insert(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION project_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	-- Create reference between product and leader
	INSERT INTO product_employee (product_id, employee_id) VALUES (
		NEW.product_id, NEW.leader_id
	);
	-- Create reference between leader and 'project-leader' group
	INSERT INTO employee_group (employee_id, groupd_id) VALUES (NEW.leader_id, (
		SELECT id FROM groups WHERE name = 'project-leader'
	));
	RETURN NEW;
END;
$$;


ALTER FUNCTION public.project_insert() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: company; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE company (
    id integer NOT NULL,
    name text NOT NULL,
    director_id integer DEFAULT 0
);


ALTER TABLE public.company OWNER TO postgres;

--
-- Name: company_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE company_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.company_id_seq OWNER TO postgres;

--
-- Name: company_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE company_id_seq OWNED BY company.id;


--
-- Name: employee; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE employee (
    id integer NOT NULL,
    name character varying(80) NOT NULL,
    surname character varying(80) NOT NULL,
    user_id integer,
    manager_id integer DEFAULT 0,
    director_id integer DEFAULT 0,
    company_id integer DEFAULT 0,
    patronymic character varying(80)
);


ALTER TABLE public.employee OWNER TO postgres;

--
-- Name: employee_group; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE employee_group (
    id integer NOT NULL,
    employee_id integer NOT NULL,
    group_id integer NOT NULL
);


ALTER TABLE public.employee_group OWNER TO postgres;

--
-- Name: employee_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE employee_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.employee_group_id_seq OWNER TO postgres;

--
-- Name: employee_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE employee_group_id_seq OWNED BY employee_group.id;


--
-- Name: employee_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE employee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.employee_id_seq OWNER TO postgres;

--
-- Name: employee_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE employee_id_seq OWNED BY employee.id;


--
-- Name: files; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE files (
    id integer NOT NULL,
    product_id integer
);


ALTER TABLE public.files OWNER TO postgres;

--
-- Name: files_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE files_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.files_id_seq OWNER TO postgres;

--
-- Name: files_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE files_id_seq OWNED BY files.id;


--
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE groups (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    description character varying(50)
);


ALTER TABLE public.groups OWNER TO postgres;

--
-- Name: group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_id_seq OWNER TO postgres;

--
-- Name: group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE group_id_seq OWNED BY groups.id;


--
-- Name: group_privilege; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE group_privilege (
    id integer NOT NULL,
    group_id integer NOT NULL,
    privilege_id character varying(20) NOT NULL
);


ALTER TABLE public.group_privilege OWNER TO postgres;

--
-- Name: group_privilege_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE group_privilege_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_privilege_id_seq OWNER TO postgres;

--
-- Name: group_privilege_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE group_privilege_id_seq OWNED BY group_privilege.id;


--
-- Name: history; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE history (
    id integer NOT NULL,
    product_id integer,
    employee_id integer,
    modified timestamp without time zone DEFAULT now()
);


ALTER TABLE public.history OWNER TO postgres;

--
-- Name: history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.history_id_seq OWNER TO postgres;

--
-- Name: history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE history_id_seq OWNED BY history.id;


--
-- Name: module; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE module (
    id integer NOT NULL,
    project_id integer,
    product_id integer
);


ALTER TABLE public.module OWNER TO postgres;

--
-- Name: module_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE module_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.module_id_seq OWNER TO postgres;

--
-- Name: module_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE module_id_seq OWNED BY module.id;


--
-- Name: privilege; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE privilege (
    id character varying(20) NOT NULL,
    name character varying(50),
    description text
);


ALTER TABLE public.privilege OWNER TO postgres;

--
-- Name: product; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE product (
    id integer NOT NULL,
    name text NOT NULL,
    company_id integer,
    creator_id integer,
    created timestamp without time zone DEFAULT now(),
    parent_id integer DEFAULT 0
);


ALTER TABLE public.product OWNER TO postgres;

--
-- Name: product_employee; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE product_employee (
    id integer NOT NULL,
    product_id integer,
    employee_id integer
);


ALTER TABLE public.product_employee OWNER TO postgres;

--
-- Name: product_employee_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE product_employee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_employee_id_seq OWNER TO postgres;

--
-- Name: product_employee_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE product_employee_id_seq OWNED BY product_employee.id;


--
-- Name: product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_id_seq OWNER TO postgres;

--
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE product_id_seq OWNED BY product.id;


--
-- Name: project; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE project (
    id integer NOT NULL,
    leader_id integer,
    product_id integer
);


ALTER TABLE public.project OWNER TO postgres;

--
-- Name: project_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE project_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.project_id_seq OWNER TO postgres;

--
-- Name: project_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE project_id_seq OWNED BY project.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    login character varying(20) NOT NULL,
    hash text NOT NULL,
    email character varying(50)
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_id_seq OWNER TO postgres;

--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE user_id_seq OWNED BY users.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY company ALTER COLUMN id SET DEFAULT nextval('company_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee ALTER COLUMN id SET DEFAULT nextval('employee_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee_group ALTER COLUMN id SET DEFAULT nextval('employee_group_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY files ALTER COLUMN id SET DEFAULT nextval('files_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_privilege ALTER COLUMN id SET DEFAULT nextval('group_privilege_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups ALTER COLUMN id SET DEFAULT nextval('group_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history ALTER COLUMN id SET DEFAULT nextval('history_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY module ALTER COLUMN id SET DEFAULT nextval('module_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product ALTER COLUMN id SET DEFAULT nextval('product_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_employee ALTER COLUMN id SET DEFAULT nextval('product_employee_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project ALTER COLUMN id SET DEFAULT nextval('project_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('user_id_seq'::regclass);


--
-- Data for Name: company; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY company (id, name, director_id) FROM stdin;
13	Jaw	10
15	Test	12
29	1	18
\.


--
-- Name: company_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('company_id_seq', 29, true);


--
-- Data for Name: employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY employee (id, name, surname, user_id, manager_id, director_id, company_id, patronymic) FROM stdin;
10	Дмитрий	Савонин	8	0	0	13	Александрович
12	Тест	Тестов	8	0	0	15	Тестович
18	3	2	8	0	0	29	4
\.


--
-- Data for Name: employee_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY employee_group (id, employee_id, group_id) FROM stdin;
1	10	3
3	12	3
24	18	3
25	18	7
\.


--
-- Name: employee_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('employee_group_id_seq', 25, true);


--
-- Name: employee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('employee_id_seq', 18, true);


--
-- Data for Name: files; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY files (id, product_id) FROM stdin;
\.


--
-- Name: files_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('files_id_seq', 1, false);


--
-- Name: group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('group_id_seq', 8, true);


--
-- Data for Name: group_privilege; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY group_privilege (id, group_id, privilege_id) FROM stdin;
\.


--
-- Name: group_privilege_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('group_privilege_id_seq', 1, false);


--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY groups (id, name, description) FROM stdin;
3	company-director	Директор
5	product-creator	Создатель
6	project-leader	Лидер
7	employee	Сотрудник
8	god	Бог
\.


--
-- Data for Name: history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY history (id, product_id, employee_id, modified) FROM stdin;
\.


--
-- Name: history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('history_id_seq', 1, false);


--
-- Data for Name: module; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY module (id, project_id, product_id) FROM stdin;
\.


--
-- Name: module_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('module_id_seq', 1, false);


--
-- Data for Name: privilege; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY privilege (id, name, description) FROM stdin;
company-create	Создать компанию	Позволяет регистрировать новые компании
company-delete	Удалить компанию	Позволяет удалить существующие компании
project-create	Создать проект	Позволяет регистрировать новые проекты
project-delete	Удалить компанию	Позволяет удалить существующие проекты
\.


--
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY product (id, name, company_id, creator_id, created, parent_id) FROM stdin;
\.


--
-- Data for Name: product_employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY product_employee (id, product_id, employee_id) FROM stdin;
\.


--
-- Name: product_employee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('product_employee_id_seq', 11, true);


--
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('product_id_seq', 23, true);


--
-- Data for Name: project; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY project (id, leader_id, product_id) FROM stdin;
\.


--
-- Name: project_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('project_id_seq', 11, true);


--
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('user_id_seq', 13, true);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY users (id, login, hash, email) FROM stdin;
8	dmitry	b59c35dc00cc3d	dmitry123@inbox.ru
10	mary	77a32d5d093b92d	mary@mary.mary
12	user	0ac82bb3d06a0	user@user.user
13	WwonG	f2db6cff8961d366	wwong123@mail.ru
\.


--
-- Name: company_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY company
    ADD CONSTRAINT company_pkey PRIMARY KEY (id);


--
-- Name: employee_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY employee_group
    ADD CONSTRAINT employee_group_pkey PRIMARY KEY (id);


--
-- Name: employee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY employee
    ADD CONSTRAINT employee_pkey PRIMARY KEY (id);


--
-- Name: files_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY files
    ADD CONSTRAINT files_pkey PRIMARY KEY (id);


--
-- Name: group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT group_pkey PRIMARY KEY (id);


--
-- Name: group_privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY group_privilege
    ADD CONSTRAINT group_privilege_pkey PRIMARY KEY (id);


--
-- Name: history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY history
    ADD CONSTRAINT history_pkey PRIMARY KEY (id);


--
-- Name: module_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY module
    ADD CONSTRAINT module_pkey PRIMARY KEY (id);


--
-- Name: privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT privilege_pkey PRIMARY KEY (id);


--
-- Name: product_employee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_employee
    ADD CONSTRAINT product_employee_pkey PRIMARY KEY (id);


--
-- Name: product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- Name: project_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);


--
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: on_company_delete; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_company_delete BEFORE DELETE ON company FOR EACH ROW EXECUTE PROCEDURE company_delete();


--
-- Name: on_company_insert; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_company_insert AFTER INSERT ON company FOR EACH ROW EXECUTE PROCEDURE company_insert();


--
-- Name: on_privilege_insert; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_privilege_insert AFTER INSERT ON privilege FOR EACH ROW EXECUTE PROCEDURE privilege_insert();


--
-- Name: on_product_delete; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_product_delete BEFORE DELETE ON product FOR EACH ROW EXECUTE PROCEDURE product_delete();


--
-- Name: on_product_insert; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_product_insert AFTER INSERT ON product FOR EACH ROW EXECUTE PROCEDURE product_insert();


--
-- Name: on_project_delete; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_project_delete BEFORE DELETE ON project FOR EACH ROW EXECUTE PROCEDURE project_delete();


--
-- Name: on_project_insert; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_project_insert AFTER INSERT ON project FOR EACH ROW EXECUTE PROCEDURE project_insert();


--
-- Name: employee_company_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee
    ADD CONSTRAINT employee_company_id_fkey FOREIGN KEY (company_id) REFERENCES company(id);


--
-- Name: employee_group_employee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee_group
    ADD CONSTRAINT employee_group_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES employee(id);


--
-- Name: employee_group_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee_group
    ADD CONSTRAINT employee_group_group_id_fkey FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- Name: employee_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee
    ADD CONSTRAINT employee_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: files_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY files
    ADD CONSTRAINT files_product_id_fkey FOREIGN KEY (product_id) REFERENCES product(id);


--
-- Name: group_privilege_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_privilege
    ADD CONSTRAINT group_privilege_group_id_fkey FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- Name: group_privilege_privilege_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_privilege
    ADD CONSTRAINT group_privilege_privilege_id_fkey FOREIGN KEY (privilege_id) REFERENCES privilege(id);


--
-- Name: history_employee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history
    ADD CONSTRAINT history_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES employee(id);


--
-- Name: history_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history
    ADD CONSTRAINT history_product_id_fkey FOREIGN KEY (product_id) REFERENCES product(id);


--
-- Name: module_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY module
    ADD CONSTRAINT module_product_id_fkey FOREIGN KEY (product_id) REFERENCES product(id);


--
-- Name: module_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY module
    ADD CONSTRAINT module_project_id_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: product_company_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product
    ADD CONSTRAINT product_company_id_fkey FOREIGN KEY (company_id) REFERENCES company(id);


--
-- Name: product_creator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product
    ADD CONSTRAINT product_creator_id_fkey FOREIGN KEY (creator_id) REFERENCES employee(id);


--
-- Name: product_employee_employee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_employee
    ADD CONSTRAINT product_employee_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES employee(id);


--
-- Name: product_employee_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_employee
    ADD CONSTRAINT product_employee_product_id_fkey FOREIGN KEY (product_id) REFERENCES product(id);


--
-- Name: project_leader_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_leader_id_fkey FOREIGN KEY (leader_id) REFERENCES employee(id);


--
-- Name: project_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_product_id_fkey FOREIGN KEY (product_id) REFERENCES product(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

