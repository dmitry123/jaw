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
	FOR j IN SELECT * FROM product WHERE company_id = OLD.id LOOP
		DELETE FROM project WHERE product_id = j.id;
	END LOOP;
	-- Delete all products
	DELETE FROM product WHERE company_id = OLD.id;
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
-- Name: employee_insert(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION employee_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
	v_director_group INT;
	v_employee_group INT;
	i employee;
	connector RECORD;
BEGIN
	FOR i IN SELECT e.* FROM company AS c JOIN employee AS e ON c.director_id = e.id LOOP
		-- Find company director and employee groups
		SELECT id INTO v_director_group FROM groups WHERE name = 'company-director';
		SELECT id INTO v_employee_group FROM groups WHERE name = 'employee';
		-- Create reference between employee and 'company-director'
		SELECT * INTO connector FROM employee_group WHERE employee_id = i.id AND group_id = v_director_group;
		IF connector IS NULL AND NEW.director_id <> 0 THEN
			INSERT INTO employee_group (employee_id, group_id) VALUES (NEW.director_id, v_director_group);
		END IF;
		-- Create reference between employee and 'employee'
		SELECT * INTO connector FROM employee_group WHERE employee_id = i.id AND group_id = v_employee_group;
		IF connector IS NULL AND NEW.director_id <> 0 THEN
			INSERT INTO employee_group (employee_id, group_id) VALUES (NEW.director_id, v_employee_group);
		END IF;
	END LOOP;
	RETURN NEW;
END;
$$;


ALTER FUNCTION public.employee_insert() OWNER TO postgres;

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
DECLARE t RECORD;
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
	SELECT * INTO t FROM product_employee WHERE product_id = NEW.id AND employee_id = NEW.creator_id;
	IF t IS NULL THEN
		INSERT INTO product_employee (product_id, employee_id) VALUES (NEW.id, NEW.creator_id);
	END IF;
	RETURN NEW;
END;
$$;


ALTER FUNCTION public.product_insert() OWNER TO postgres;

--
-- Name: project_after_delete(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION project_after_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	DELETE FROM product WHERE id = OLD.product_id;
	RETURN OLD;
END;
$$;


ALTER FUNCTION public.project_after_delete() OWNER TO postgres;

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
	INSERT INTO employee_group (employee_id, group_id) VALUES (NEW.leader_id, (
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
    patronymic character varying(80),
    join_date timestamp without time zone DEFAULT now()
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
-- Name: message; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE message (
    id integer NOT NULL,
    sender_id integer,
    receiver_id integer,
    message text
);


ALTER TABLE public.message OWNER TO postgres;

--
-- Name: message_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.message_id_seq OWNER TO postgres;

--
-- Name: message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE message_id_seq OWNED BY message.id;


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
-- Name: request; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE request (
    id integer NOT NULL,
    receiver_id integer,
    sender_id integer,
    privilege_id character varying(20),
    message text,
    product_id integer DEFAULT 0
);


ALTER TABLE public.request OWNER TO postgres;

--
-- Name: request_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE request_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.request_id_seq OWNER TO postgres;

--
-- Name: request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE request_id_seq OWNED BY request.id;


--
-- Name: ticket; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ticket (
    id integer NOT NULL,
    created_by integer,
    owner_id integer,
    precedence integer,
    product_id integer,
    project_id integer,
    description text,
    parent_id integer DEFAULT 0,
    creation_date timestamp without time zone DEFAULT now(),
    name text
);


ALTER TABLE public.ticket OWNER TO postgres;

--
-- Name: ticket_attachment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ticket_attachment (
    id integer NOT NULL,
    file_id integer,
    ticket_id integer,
    comment_id integer DEFAULT 0,
    creation_date timestamp without time zone DEFAULT now()
);


ALTER TABLE public.ticket_attachment OWNER TO postgres;

--
-- Name: ticket_attachment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ticket_attachment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ticket_attachment_id_seq OWNER TO postgres;

--
-- Name: ticket_attachment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ticket_attachment_id_seq OWNED BY ticket_attachment.id;


--
-- Name: ticket_comment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ticket_comment (
    id integer NOT NULL,
    ticket_id integer,
    owner_id integer,
    message text,
    creation_date timestamp without time zone DEFAULT now()
);


ALTER TABLE public.ticket_comment OWNER TO postgres;

--
-- Name: ticket_comment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ticket_comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ticket_comment_id_seq OWNER TO postgres;

--
-- Name: ticket_comment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ticket_comment_id_seq OWNED BY ticket_comment.id;


--
-- Name: ticket_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ticket_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ticket_id_seq OWNER TO postgres;

--
-- Name: ticket_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ticket_id_seq OWNED BY ticket.id;


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

ALTER TABLE ONLY message ALTER COLUMN id SET DEFAULT nextval('message_id_seq'::regclass);


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

ALTER TABLE ONLY request ALTER COLUMN id SET DEFAULT nextval('request_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket ALTER COLUMN id SET DEFAULT nextval('ticket_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket_attachment ALTER COLUMN id SET DEFAULT nextval('ticket_attachment_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket_comment ALTER COLUMN id SET DEFAULT nextval('ticket_comment_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('user_id_seq'::regclass);


--
-- Data for Name: company; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY company (id, name, director_id) FROM stdin;
51	Catstrap	39
52	ArsDraw	40
48	Chtulhu	36
31	Jaw	20
49	Admin	37
50	Justice	38
53	Иван & Ко	41
\.


--
-- Name: company_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('company_id_seq', 53, true);


--
-- Data for Name: employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY employee (id, name, surname, user_id, manager_id, director_id, company_id, patronymic, join_date) FROM stdin;
20	Дмитрий	Савонин	8	0	0	31	Александрович	2015-01-01 15:52:07.394
36	Мария	Гущина	17	0	0	48	Олеговна	2015-01-01 15:52:07.394
37	Админ	Админов	16	0	0	49	Админович	2015-01-01 15:52:07.394
38	Анастасия	Романова	15	0	0	50	Игоревна	2015-01-01 15:52:07.394
39	Надежда	Левенкова	18	0	0	51	Сергеевна	2015-01-01 15:52:07.394
40	Григорий	Распутин	21	0	0	52	Ефимович	2015-01-01 15:52:07.394
41	Иван	Иванов	23	0	0	53	Иванович	2015-01-01 15:52:07.394
53	Мария	Гущина	17	0	0	0	Олеговна	2015-01-02 13:42:14.585
58	Тест	Тестов	25	0	0	31	Тестович	2015-01-02 14:50:48.12
59	Анастасия	Романова	15	0	0	31	Игоревна	2015-01-07 16:06:03.81
\.


--
-- Data for Name: employee_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY employee_group (id, employee_id, group_id) FROM stdin;
72	36	3
73	36	7
74	37	3
75	37	7
76	37	5
77	37	7
78	37	6
79	36	5
80	36	7
81	36	6
82	38	3
83	38	7
84	38	5
85	38	7
86	38	6
87	39	3
88	39	7
89	39	5
90	39	7
91	39	6
92	40	3
93	40	7
94	40	5
95	40	7
96	40	6
100	41	3
33	20	3
101	41	7
35	20	8
112	41	5
113	41	7
114	41	6
115	20	7
116	20	6
118	58	7
119	59	7
120	38	7
\.


--
-- Name: employee_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('employee_group_id_seq', 120, true);


--
-- Name: employee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('employee_id_seq', 59, true);


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
36	8	jaw/admin
39	8	company/leave
40	8	company/join
41	8	product/join
42	8	product/leave
43	8	company/register
44	8	company/delete
45	8	project/register
46	8	project/delete
47	3	company/leave
48	3	company/join
49	3	product/join
50	3	product/leave
51	3	company/delete
52	3	project/register
53	3	project/delete
54	5	product/join
55	6	product/join
56	6	product/leave
57	8	ticket/register
58	8	ticket/delete
59	6	ticket/register
60	6	ticket/delete
\.


--
-- Name: group_privilege_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('group_privilege_id_seq', 61, true);


--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY groups (id, name, description) FROM stdin;
3	company-director	Директор
5	product-creator	Создатель
6	project-leader	Лидер
7	employee	Сотрудник
8	god	Система
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
-- Data for Name: message; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY message (id, sender_id, receiver_id, message) FROM stdin;
1	20	20	Приве, как твои дела? Веришь ты мечатм. Носишь майку... Кто вместо меня, засыпает там, на твоем плече, как твои дела, все равно узнаешь - это моя лучшая зима!
2	20	20	Приве, как твои дела? Веришь ты мечатм. Носишь майку... Кто вместо меня, засыпает там, на твоем плече, как твои дела, все равно узнаешь - это моя лучшая зима!
\.


--
-- Name: message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('message_id_seq', 2, true);


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
ticket/delete	Удалить задачу	Может удалять задачи
company/leave	Удалить сотрудника из компании	Может удалять сотрудников из компании
company/join	Добавить сотрудника в компанию	Может добавлять сотрудников в компанию
product/join	Подключить сотрудника к разработке	Может подключать сотрудников к продуктам
product/leave	Отключить сотрудника от разработки	Может отключать сотрудников от разработки
company/register	Зарегистрировать компанию	Может создавать компании
company/delete	Удалить компанию	Может удалить компанию
jaw/admin	Система	Может администрировать систему
project/register	Зарегистрировать проект	Может регистрировать проекты
project/delete	Удалить проект	Может удалить проект
ticket/register	Добавить задачу	Может регистрировать задачи
\.


--
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY product (id, name, company_id, creator_id, created, parent_id) FROM stdin;
31	Jaw	31	20	2014-12-27 19:47:30.369	0
34	Admin	49	37	2014-12-27 21:52:06.21	0
35	Cthulhu	48	36	2014-12-27 21:52:24.348	0
36	Justice	50	38	2014-12-27 21:52:49.043	0
37	Catstrap	51	39	2014-12-27 21:54:03.099	0
38	Starcraft	52	40	2014-12-27 21:54:59.531	0
45	Проект	53	41	2014-12-28 14:13:19.34	0
\.


--
-- Data for Name: product_employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY product_employee (id, product_id, employee_id) FROM stdin;
23	31	20
29	34	37
31	35	36
33	36	38
35	37	39
37	38	40
43	45	41
44	45	41
55	31	58
56	31	59
\.


--
-- Name: product_employee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('product_employee_id_seq', 56, true);


--
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('product_id_seq', 45, true);


--
-- Data for Name: project; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY project (id, leader_id, product_id) FROM stdin;
16	20	31
19	37	34
20	36	35
21	38	36
22	39	37
23	40	38
25	41	45
\.


--
-- Name: project_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('project_id_seq', 25, true);


--
-- Data for Name: request; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY request (id, receiver_id, sender_id, privilege_id, message, product_id) FROM stdin;
\.


--
-- Name: request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('request_id_seq', 31, true);


--
-- Data for Name: ticket; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ticket (id, created_by, owner_id, precedence, product_id, project_id, description, parent_id, creation_date, name) FROM stdin;
\.


--
-- Data for Name: ticket_attachment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ticket_attachment (id, file_id, ticket_id, comment_id, creation_date) FROM stdin;
\.


--
-- Name: ticket_attachment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ticket_attachment_id_seq', 1, false);


--
-- Data for Name: ticket_comment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ticket_comment (id, ticket_id, owner_id, message, creation_date) FROM stdin;
\.


--
-- Name: ticket_comment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ticket_comment_id_seq', 1, false);


--
-- Name: ticket_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ticket_id_seq', 1, false);


--
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('user_id_seq', 25, true);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY users (id, login, hash, email) FROM stdin;
8	dmitry	b59c35dc00cc3d	dmitry123@inbox.ru
16	admin	2e2ba6ba9c2a6b4a	admin@admin.ru
15	nastya	4627fe60608dfb8d	nastya@nastya.ru
18	nadya	4d69379863de90dc	nadya@nadya.ru
17	mary	77a32d5d093b92d	mary@mary.ru
21	arseniy	930ca2bf329f887	arseniy@arseniy.ru
23	shu	9ea9c3f1aa98741e	shu@shu.ru
24	jeniya	a8b7cc89a9219a0	jeniya@jeniya.ru
25	test	912f34f975f62df1	test@test.ru
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
-- Name: message_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY message
    ADD CONSTRAINT message_pkey PRIMARY KEY (id);


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
-- Name: request_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY request
    ADD CONSTRAINT request_pkey PRIMARY KEY (id);


--
-- Name: ticket_attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ticket_attachment
    ADD CONSTRAINT ticket_attachment_pkey PRIMARY KEY (id);


--
-- Name: ticket_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ticket_comment
    ADD CONSTRAINT ticket_comment_pkey PRIMARY KEY (id);


--
-- Name: ticket_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ticket
    ADD CONSTRAINT ticket_pkey PRIMARY KEY (id);


--
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: on_after_project_delete; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_after_project_delete AFTER DELETE ON project FOR EACH ROW EXECUTE PROCEDURE project_after_delete();


--
-- Name: on_company_delete; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_company_delete BEFORE DELETE ON company FOR EACH ROW EXECUTE PROCEDURE company_delete();


--
-- Name: on_company_insert; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_company_insert AFTER INSERT ON company FOR EACH ROW EXECUTE PROCEDURE company_insert();


--
-- Name: on_company_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_company_update AFTER UPDATE ON company FOR EACH ROW EXECUTE PROCEDURE employee_insert();


--
-- Name: on_employee_insert; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_employee_insert AFTER INSERT ON employee FOR EACH ROW EXECUTE PROCEDURE employee_insert();


--
-- Name: on_employee_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER on_employee_update AFTER UPDATE ON employee FOR EACH ROW EXECUTE PROCEDURE employee_insert();


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
-- Name: message_receiver_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY message
    ADD CONSTRAINT message_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES employee(id);


--
-- Name: message_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY message
    ADD CONSTRAINT message_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES employee(id);


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
-- Name: request_privilege_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY request
    ADD CONSTRAINT request_privilege_id_fkey FOREIGN KEY (privilege_id) REFERENCES privilege(id);


--
-- Name: request_receiver_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY request
    ADD CONSTRAINT request_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES employee(id);


--
-- Name: request_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY request
    ADD CONSTRAINT request_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES employee(id);


--
-- Name: ticket_attachment_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket_attachment
    ADD CONSTRAINT ticket_attachment_file_id_fkey FOREIGN KEY (file_id) REFERENCES files(id);


--
-- Name: ticket_attachment_ticket_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket_attachment
    ADD CONSTRAINT ticket_attachment_ticket_id_fkey FOREIGN KEY (ticket_id) REFERENCES ticket(id);


--
-- Name: ticket_comment_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket_comment
    ADD CONSTRAINT ticket_comment_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES employee(id);


--
-- Name: ticket_comment_ticket_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket_comment
    ADD CONSTRAINT ticket_comment_ticket_id_fkey FOREIGN KEY (ticket_id) REFERENCES ticket(id);


--
-- Name: ticket_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket
    ADD CONSTRAINT ticket_created_by_fkey FOREIGN KEY (created_by) REFERENCES employee(id);


--
-- Name: ticket_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket
    ADD CONSTRAINT ticket_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES employee(id);


--
-- Name: ticket_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket
    ADD CONSTRAINT ticket_product_id_fkey FOREIGN KEY (product_id) REFERENCES product(id);


--
-- Name: ticket_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticket
    ADD CONSTRAINT ticket_project_id_fkey FOREIGN KEY (project_id) REFERENCES project(id);


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

