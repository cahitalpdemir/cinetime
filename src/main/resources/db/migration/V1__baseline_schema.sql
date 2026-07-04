--
-- PostgreSQL database dump
--

-- Dumped from database version 14.18
-- Dumped by pg_dump version 16.9

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

-- *not* creating schema, since initdb creates it


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: booking_seats; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.booking_seats (
    id bigint NOT NULL,
    price double precision NOT NULL,
    booking_id bigint NOT NULL,
    seat_id bigint NOT NULL,
    showtime_id bigint NOT NULL
);


--
-- Name: booking_seats_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.booking_seats_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: booking_seats_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.booking_seats_id_seq OWNED BY public.booking_seats.id;


--
-- Name: bookings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bookings (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    status character varying(255) NOT NULL,
    total_price double precision NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    showtime_id bigint NOT NULL,
    user_id bigint NOT NULL
);


--
-- Name: bookings_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.bookings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bookings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.bookings_id_seq OWNED BY public.bookings.id;


--
-- Name: cinemas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cinemas (
    id bigint NOT NULL,
    address character varying(255) NOT NULL,
    city character varying(20) NOT NULL,
    district character varying(20) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    name character varying(25) NOT NULL,
    phone character varying(255) NOT NULL
);


--
-- Name: cinemas_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cinemas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cinemas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cinemas_id_seq OWNED BY public.cinemas.id;


--
-- Name: halls; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.halls (
    id bigint NOT NULL,
    hall_type character varying(20) NOT NULL,
    name character varying(25) NOT NULL,
    rows integer NOT NULL,
    seats_per_row integer NOT NULL,
    cinema_id bigint NOT NULL
);


--
-- Name: halls_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.halls_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: halls_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.halls_id_seq OWNED BY public.halls.id;


--
-- Name: movie_cast; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movie_cast (
    movie_id bigint NOT NULL,
    actor character varying(255)
);


--
-- Name: movie_formats; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movie_formats (
    movie_id bigint NOT NULL,
    format character varying(255)
);


--
-- Name: movies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movies (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    director character varying(255) NOT NULL,
    duration integer NOT NULL,
    genre character varying(255) NOT NULL,
    rating double precision,
    release_date date NOT NULL,
    slug character varying(20) NOT NULL,
    special_halls character varying(255),
    status integer NOT NULL,
    summary character varying(300) NOT NULL,
    title character varying(100) NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: movies_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.movies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: movies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.movies_id_seq OWNED BY public.movies.id;


--
-- Name: payments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.payments (
    id bigint NOT NULL,
    amount double precision NOT NULL,
    created_at timestamp without time zone NOT NULL,
    payment_method character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    transaction_id character varying(255) NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    booking_id bigint NOT NULL
);


--
-- Name: payments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.payments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: payments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.payments_id_seq OWNED BY public.payments.id;


--
-- Name: roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    role_name character varying(255) NOT NULL
);


--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- Name: seats; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.seats (
    id bigint NOT NULL,
    row_letter character varying(2) NOT NULL,
    seat_number integer NOT NULL,
    seat_type character varying(255) NOT NULL,
    hall_id bigint NOT NULL
);


--
-- Name: seats_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.seats_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seats_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.seats_id_seq OWNED BY public.seats.id;


--
-- Name: showtimes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.showtimes (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    date date NOT NULL,
    end_time time without time zone NOT NULL,
    format character varying(20) NOT NULL,
    language character varying(50) NOT NULL,
    price double precision NOT NULL,
    start_time time without time zone NOT NULL,
    status character varying(255) NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    hall_id bigint NOT NULL,
    movie_id bigint NOT NULL
);


--
-- Name: showtimes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.showtimes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: showtimes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.showtimes_id_seq OWNED BY public.showtimes.id;


--
-- Name: tickets; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tickets (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    qr_code character varying(255) NOT NULL,
    ticket_number character varying(255) NOT NULL,
    booking_id bigint NOT NULL,
    seat_id bigint NOT NULL
);


--
-- Name: tickets_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tickets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tickets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tickets_id_seq OWNED BY public.tickets.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    birth_date date NOT NULL,
    built_in boolean,
    created_at timestamp without time zone NOT NULL,
    email character varying(255) NOT NULL,
    gender character varying(255) NOT NULL,
    name character varying(20) NOT NULL,
    password character varying(255) NOT NULL,
    phone_number character varying(255) NOT NULL,
    reset_password_token character varying(255),
    reset_password_token_expire_date timestamp without time zone,
    surname character varying(25) NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    role_id bigint NOT NULL
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: booking_seats id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking_seats ALTER COLUMN id SET DEFAULT nextval('public.booking_seats_id_seq'::regclass);


--
-- Name: bookings id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bookings ALTER COLUMN id SET DEFAULT nextval('public.bookings_id_seq'::regclass);


--
-- Name: cinemas id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cinemas ALTER COLUMN id SET DEFAULT nextval('public.cinemas_id_seq'::regclass);


--
-- Name: halls id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.halls ALTER COLUMN id SET DEFAULT nextval('public.halls_id_seq'::regclass);


--
-- Name: movies id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movies ALTER COLUMN id SET DEFAULT nextval('public.movies_id_seq'::regclass);


--
-- Name: payments id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments ALTER COLUMN id SET DEFAULT nextval('public.payments_id_seq'::regclass);


--
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- Name: seats id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.seats ALTER COLUMN id SET DEFAULT nextval('public.seats_id_seq'::regclass);


--
-- Name: showtimes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.showtimes ALTER COLUMN id SET DEFAULT nextval('public.showtimes_id_seq'::regclass);


--
-- Name: tickets id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets ALTER COLUMN id SET DEFAULT nextval('public.tickets_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: booking_seats booking_seats_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking_seats
    ADD CONSTRAINT booking_seats_pkey PRIMARY KEY (id);


--
-- Name: bookings bookings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT bookings_pkey PRIMARY KEY (id);


--
-- Name: cinemas cinemas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cinemas
    ADD CONSTRAINT cinemas_pkey PRIMARY KEY (id);


--
-- Name: halls halls_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.halls
    ADD CONSTRAINT halls_pkey PRIMARY KEY (id);


--
-- Name: movies movies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movies
    ADD CONSTRAINT movies_pkey PRIMARY KEY (id);


--
-- Name: payments payments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT payments_pkey PRIMARY KEY (id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: seats seats_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.seats
    ADD CONSTRAINT seats_pkey PRIMARY KEY (id);


--
-- Name: showtimes showtimes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.showtimes
    ADD CONSTRAINT showtimes_pkey PRIMARY KEY (id);


--
-- Name: tickets tickets_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_pkey PRIMARY KEY (id);


--
-- Name: tickets uk_136k8tqvcn833mi3tjgqktnx2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT uk_136k8tqvcn833mi3tjgqktnx2 UNIQUE (qr_code);


--
-- Name: movies uk_3xo75bcan34s1o1fldt94skss; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movies
    ADD CONSTRAINT uk_3xo75bcan34s1o1fldt94skss UNIQUE (slug);


--
-- Name: tickets uk_4ks48wgrew48dpkh0wd1rbe2b; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT uk_4ks48wgrew48dpkh0wd1rbe2b UNIQUE (ticket_number);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: roles uk_716hgxp60ym1lifrdgp67xt5k; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT uk_716hgxp60ym1lifrdgp67xt5k UNIQUE (role_name);


--
-- Name: payments uk_lryndveuwa4k5qthti0pkmtlx; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT uk_lryndveuwa4k5qthti0pkmtlx UNIQUE (transaction_id);


--
-- Name: payments uk_nuscjm6x127hkb15kcb8n56wo; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT uk_nuscjm6x127hkb15kcb8n56wo UNIQUE (booking_id);


--
-- Name: seats uk_seat_hall_row_number; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.seats
    ADD CONSTRAINT uk_seat_hall_row_number UNIQUE (hall_id, row_letter, seat_number);


--
-- Name: cinemas uk_sln7noqfuryvid2utui93hrua; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cinemas
    ADD CONSTRAINT uk_sln7noqfuryvid2utui93hrua UNIQUE (phone);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: tickets fk1f6n3pv4b80wl6gj4ra32ctxk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT fk1f6n3pv4b80wl6gj4ra32ctxk FOREIGN KEY (seat_id) REFERENCES public.seats(id);


--
-- Name: seats fk3jtfe0f60bcpbavj4mctjeasw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.seats
    ADD CONSTRAINT fk3jtfe0f60bcpbavj4mctjeasw FOREIGN KEY (hall_id) REFERENCES public.halls(id);


--
-- Name: movie_formats fk6ogd2ycfpfa0905gvxj3v71wr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movie_formats
    ADD CONSTRAINT fk6ogd2ycfpfa0905gvxj3v71wr FOREIGN KEY (movie_id) REFERENCES public.movies(id);


--
-- Name: payments fkc52o2b1jkxttngufqp3t7jr3h; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fkc52o2b1jkxttngufqp3t7jr3h FOREIGN KEY (booking_id) REFERENCES public.bookings(id);


--
-- Name: bookings fkc7q4u7vleq90vlvy8c7lmwtyl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT fkc7q4u7vleq90vlvy8c7lmwtyl FOREIGN KEY (showtime_id) REFERENCES public.showtimes(id);


--
-- Name: tickets fkefja4avuu7g29t78mxifrsynb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT fkefja4avuu7g29t78mxifrsynb FOREIGN KEY (booking_id) REFERENCES public.bookings(id);


--
-- Name: showtimes fkeltpyuei1d5g3n6ikpsjwwil6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.showtimes
    ADD CONSTRAINT fkeltpyuei1d5g3n6ikpsjwwil6 FOREIGN KEY (movie_id) REFERENCES public.movies(id);


--
-- Name: bookings fkeyog2oic85xg7hsu2je2lx3s6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT fkeyog2oic85xg7hsu2je2lx3s6 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: movie_cast fkh3ht4nyhscwpt25ikwdu7lfqj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movie_cast
    ADD CONSTRAINT fkh3ht4nyhscwpt25ikwdu7lfqj FOREIGN KEY (movie_id) REFERENCES public.movies(id);


--
-- Name: booking_seats fkm2vak166qv8osqwe5qcxsn1p; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking_seats
    ADD CONSTRAINT fkm2vak166qv8osqwe5qcxsn1p FOREIGN KEY (seat_id) REFERENCES public.seats(id);


--
-- Name: booking_seats fkmbi9ciapn0nvat63t0a8tv478; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking_seats
    ADD CONSTRAINT fkmbi9ciapn0nvat63t0a8tv478 FOREIGN KEY (booking_id) REFERENCES public.bookings(id);


--
-- Name: users fkp56c1712k691lhsyewcssf40f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: halls fkpst04yq0t1iyprvitond7ly34; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.halls
    ADD CONSTRAINT fkpst04yq0t1iyprvitond7ly34 FOREIGN KEY (cinema_id) REFERENCES public.cinemas(id);


--
-- Name: booking_seats fkq5o0d0yjwuef99dkoun0642ds; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking_seats
    ADD CONSTRAINT fkq5o0d0yjwuef99dkoun0642ds FOREIGN KEY (showtime_id) REFERENCES public.showtimes(id);


--
-- Name: showtimes fkqsy0b55f69acjhchd4cdms9fj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.showtimes
    ADD CONSTRAINT fkqsy0b55f69acjhchd4cdms9fj FOREIGN KEY (hall_id) REFERENCES public.halls(id);


--
-- PostgreSQL database dump complete
--
