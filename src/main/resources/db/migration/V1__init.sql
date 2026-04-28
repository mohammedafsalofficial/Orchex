--
-- PostgreSQL database dump
--


-- Dumped from database version 17.9 (Debian 17.9-1.pgdg13+1)
-- Dumped by pg_dump version 17.9 (Debian 17.9-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: task_definitions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.task_definitions (
    id uuid NOT NULL,
    config_json text,
    dependencies text,
    name character varying(255) NOT NULL,
    retry_limit integer,
    task_type character varying(255),
    timeout_seconds integer,
    workflow_definition_id uuid,
    CONSTRAINT task_definitions_task_type_check CHECK (((task_type)::text = ANY ((ARRAY['HTTP'::character varying, 'WORKER'::character varying, 'SCRIPT'::character varying, 'DATABASE'::character varying, 'EVENT'::character varying])::text[])))
);


--
-- Name: task_executions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.task_executions (
    id uuid NOT NULL,
    completed_at timestamp(6) without time zone,
    created_at timestamp(6) without time zone NOT NULL,
    error_message text,
    input_payload text,
    output_payload text,
    retry_count integer NOT NULL,
    started_at timestamp(6) without time zone,
    status character varying(255) NOT NULL,
    worker_id character varying(255),
    task_definition_id uuid NOT NULL,
    workflow_execution_id uuid NOT NULL,
    CONSTRAINT task_executions_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'RUNNING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'SKIPPED'::character varying, 'CANCELLED'::character varying, 'TIMED_OUT'::character varying])::text[])))
);


--
-- Name: workflow_definitions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflow_definitions (
    id uuid NOT NULL,
    active boolean,
    created_at timestamp(6) without time zone,
    description character varying(255),
    name character varying(255) NOT NULL,
    version integer NOT NULL
);


--
-- Name: workflow_executions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflow_executions (
    id uuid NOT NULL,
    completed_at timestamp(6) without time zone,
    correlation_id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    error_message text,
    started_at timestamp(6) without time zone,
    status character varying(255) NOT NULL,
    triggered_by character varying(255) NOT NULL,
    workflow_definition_id uuid NOT NULL,
    CONSTRAINT workflow_executions_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'RUNNING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'CANCELLED'::character varying, 'TIMED_OUT'::character varying])::text[])))
);

--
-- Name: task_definitions task_definitions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_definitions
    ADD CONSTRAINT task_definitions_pkey PRIMARY KEY (id);


--
-- Name: task_executions task_executions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_executions
    ADD CONSTRAINT task_executions_pkey PRIMARY KEY (id);


--
-- Name: workflow_definitions ukkxk03grjx8kd3de3kf719522r; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_definitions
    ADD CONSTRAINT ukkxk03grjx8kd3de3kf719522r UNIQUE (name);


--
-- Name: workflow_definitions workflow_definitions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_definitions
    ADD CONSTRAINT workflow_definitions_pkey PRIMARY KEY (id);


--
-- Name: workflow_executions workflow_executions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_executions
    ADD CONSTRAINT workflow_executions_pkey PRIMARY KEY (id);

--
-- Name: idx_task_execution_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_task_execution_status ON public.task_executions USING btree (status);


--
-- Name: idx_task_execution_worker; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_task_execution_worker ON public.task_executions USING btree (worker_id);


--
-- Name: idx_task_execution_workflow; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_task_execution_workflow ON public.task_executions USING btree (workflow_execution_id);


--
-- Name: idx_workflow_execution_definition; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_workflow_execution_definition ON public.workflow_executions USING btree (workflow_definition_id);


--
-- Name: idx_workflow_execution_started_at; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_workflow_execution_started_at ON public.workflow_executions USING btree (started_at);


--
-- Name: idx_workflow_execution_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_workflow_execution_status ON public.workflow_executions USING btree (status);


--
-- Name: task_executions fk1kx1f159k26in5q2mxdwx5fcl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_executions
    ADD CONSTRAINT fk1kx1f159k26in5q2mxdwx5fcl FOREIGN KEY (task_definition_id) REFERENCES public.task_definitions(id);


--
-- Name: task_definitions fkf9y58bp6k025ammy9sdv7vedm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_definitions
    ADD CONSTRAINT fkf9y58bp6k025ammy9sdv7vedm FOREIGN KEY (workflow_definition_id) REFERENCES public.workflow_definitions(id);


--
-- Name: workflow_executions fkhc1hde689ggroypkm18kkr4kb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_executions
    ADD CONSTRAINT fkhc1hde689ggroypkm18kkr4kb FOREIGN KEY (workflow_definition_id) REFERENCES public.workflow_definitions(id);


--
-- Name: task_executions fkmdy1lj45fuprn0g6b8qmevln2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_executions
    ADD CONSTRAINT fkmdy1lj45fuprn0g6b8qmevln2 FOREIGN KEY (workflow_execution_id) REFERENCES public.workflow_executions(id);


--
-- PostgreSQL database dump complete
--


