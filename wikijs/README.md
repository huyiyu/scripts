# wiki js 搭建

## 保存镜像
```bash
# 1. 保存镜像
docker save postgres:11-alpine ghcr.io/requarks/wiki:2 > wiki.tar
# 2. 如果仍然太大可以用gzip压缩 可压缩到大概195M 
gzip -9 wiki.tar
```
## 导入镜像
```bash
docker load < wiki.tar.gz
```
## 新建docker-compose 文件
```yaml
version: "3"
services:
  db:
    image: postgres:11-alpine
    container_name: postgresql
    environment:
    - TZ=Asia/Shanghai
    - POSTGRES_DB=wiki
    - POSTGRES_PASSWORD=wikijsrocks
    - POSTGRES_USER=wikijs
    logging:
      driver: "none"
    restart: unless-stopped
    volumes:
      - ./data/db:/var/lib/postgresql/data
  wiki:
    image: ghcr.io/requarks/wiki:2
    container_name: wiki
    depends_on:
      - db
    environment:
    - TZ=Asia/Shanghai
    - DB_TYPE=postgres
    - DB_HOST=db
    - DB_PORT=5432
    - DB_USER=wikijs
    - DB_PASS=wikijsrocks
    - DB_NAME=wiki
    restart: unless-stopped
    ports:
      - "80:3000"
```
## 新建数据目录并启动
```bash
mkdir -p ./data/db
docker- compose up -d
```
## 访问页面
### 设置 admin 账号密码
![setup](./setup.png)

* admin 邮箱:admin@jfy.com
* 密码: hc1w0U1k4%BY6%mU%Gq4HVf
* 主站域名: http://wiki.fjjfypt.com
### 登陆
![login](./login.png)
### 设置语言和搜索引擎
![home](./home.png)
![language](./language.png)
![search](./search.png)
## 备份
* postgreSql 的数据目录 data/db 的迁移
* 使用postgres 提供的[pg_dump]()

```bash
# 备份数据 类似mysqldump
docker-compose exec db pg_dump -f /var/lib/postgresql/data/backup.sql -U wikijs  wiki
# 还原数据
docker exec -i postgresql psql -U wikijs  wiki data/backup.sql
```

## 离线数据
```sql
--
-- PostgreSQL database dump
--

-- Dumped from database version 11.15
-- Dumped by pg_dump version 11.15

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
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: analytics; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.analytics (
    key character varying(255) NOT NULL,
    "isEnabled" boolean DEFAULT false NOT NULL,
    config json NOT NULL
);


ALTER TABLE public.analytics OWNER TO wikijs;

--
-- Name: apiKeys; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."apiKeys" (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    key text NOT NULL,
    expiration character varying(255) NOT NULL,
    "isRevoked" boolean DEFAULT false NOT NULL,
    "createdAt" character varying(255) NOT NULL,
    "updatedAt" character varying(255) NOT NULL
);


ALTER TABLE public."apiKeys" OWNER TO wikijs;

--
-- Name: apiKeys_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."apiKeys_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."apiKeys_id_seq" OWNER TO wikijs;

--
-- Name: apiKeys_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."apiKeys_id_seq" OWNED BY public."apiKeys".id;


--
-- Name: assetData; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."assetData" (
    id integer NOT NULL,
    data bytea NOT NULL
);


ALTER TABLE public."assetData" OWNER TO wikijs;

--
-- Name: assetFolders; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."assetFolders" (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    slug character varying(255) NOT NULL,
    "parentId" integer
);


ALTER TABLE public."assetFolders" OWNER TO wikijs;

--
-- Name: assetFolders_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."assetFolders_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."assetFolders_id_seq" OWNER TO wikijs;

--
-- Name: assetFolders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."assetFolders_id_seq" OWNED BY public."assetFolders".id;


--
-- Name: assets; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.assets (
    id integer NOT NULL,
    filename character varying(255) NOT NULL,
    hash character varying(255) NOT NULL,
    ext character varying(255) NOT NULL,
    kind text DEFAULT 'binary'::text NOT NULL,
    mime character varying(255) DEFAULT 'application/octet-stream'::character varying NOT NULL,
    "fileSize" integer,
    metadata json,
    "createdAt" character varying(255) NOT NULL,
    "updatedAt" character varying(255) NOT NULL,
    "folderId" integer,
    "authorId" integer,
    CONSTRAINT assets_kind_check CHECK ((kind = ANY (ARRAY['binary'::text, 'image'::text])))
);


ALTER TABLE public.assets OWNER TO wikijs;

--
-- Name: COLUMN assets."fileSize"; Type: COMMENT; Schema: public; Owner: wikijs
--

COMMENT ON COLUMN public.assets."fileSize" IS 'In kilobytes';


--
-- Name: assets_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public.assets_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.assets_id_seq OWNER TO wikijs;

--
-- Name: assets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public.assets_id_seq OWNED BY public.assets.id;


--
-- Name: authentication; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.authentication (
    key character varying(255) NOT NULL,
    "isEnabled" boolean DEFAULT false NOT NULL,
    config json NOT NULL,
    "selfRegistration" boolean DEFAULT false NOT NULL,
    "domainWhitelist" json NOT NULL,
    "autoEnrollGroups" json NOT NULL,
    "order" integer DEFAULT 0 NOT NULL,
    "strategyKey" character varying(255) DEFAULT ''::character varying NOT NULL,
    "displayName" character varying(255) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.authentication OWNER TO wikijs;

--
-- Name: brute; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.brute (
    key character varying(255),
    "firstRequest" bigint,
    "lastRequest" bigint,
    lifetime bigint,
    count integer
);


ALTER TABLE public.brute OWNER TO wikijs;

--
-- Name: commentProviders; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."commentProviders" (
    key character varying(255) NOT NULL,
    "isEnabled" boolean DEFAULT false NOT NULL,
    config json NOT NULL
);


ALTER TABLE public."commentProviders" OWNER TO wikijs;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.comments (
    id integer NOT NULL,
    content text NOT NULL,
    "createdAt" character varying(255) NOT NULL,
    "updatedAt" character varying(255) NOT NULL,
    "pageId" integer,
    "authorId" integer,
    render text DEFAULT ''::text NOT NULL,
    name character varying(255) DEFAULT ''::character varying NOT NULL,
    email character varying(255) DEFAULT ''::character varying NOT NULL,
    ip character varying(255) DEFAULT ''::character varying NOT NULL,
    "replyTo" integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.comments OWNER TO wikijs;

--
-- Name: comments_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public.comments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.comments_id_seq OWNER TO wikijs;

--
-- Name: comments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public.comments_id_seq OWNED BY public.comments.id;


--
-- Name: editors; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.editors (
    key character varying(255) NOT NULL,
    "isEnabled" boolean DEFAULT false NOT NULL,
    config json NOT NULL
);


ALTER TABLE public.editors OWNER TO wikijs;

--
-- Name: groups; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.groups (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    permissions json NOT NULL,
    "pageRules" json NOT NULL,
    "isSystem" boolean DEFAULT false NOT NULL,
    "createdAt" character varying(255) NOT NULL,
    "updatedAt" character varying(255) NOT NULL,
    "redirectOnLogin" character varying(255) DEFAULT '/'::character varying NOT NULL
);


ALTER TABLE public.groups OWNER TO wikijs;

--
-- Name: groups_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public.groups_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.groups_id_seq OWNER TO wikijs;

--
-- Name: groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public.groups_id_seq OWNED BY public.groups.id;


--
-- Name: locales; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.locales (
    code character varying(5) NOT NULL,
    strings json,
    "isRTL" boolean DEFAULT false NOT NULL,
    name character varying(255) NOT NULL,
    "nativeName" character varying(255) NOT NULL,
    availability integer DEFAULT 0 NOT NULL,
    "createdAt" character varying(255) NOT NULL,
    "updatedAt" character varying(255) NOT NULL
);


ALTER TABLE public.locales OWNER TO wikijs;

--
-- Name: loggers; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.loggers (
    key character varying(255) NOT NULL,
    "isEnabled" boolean DEFAULT false NOT NULL,
    level character varying(255) DEFAULT 'warn'::character varying NOT NULL,
    config json
);


ALTER TABLE public.loggers OWNER TO wikijs;

--
-- Name: migrations; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.migrations (
    id integer NOT NULL,
    name character varying(255),
    batch integer,
    migration_time timestamp with time zone
);


ALTER TABLE public.migrations OWNER TO wikijs;

--
-- Name: migrations_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public.migrations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.migrations_id_seq OWNER TO wikijs;

--
-- Name: migrations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public.migrations_id_seq OWNED BY public.migrations.id;


--
-- Name: migrations_lock; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.migrations_lock (
    index integer NOT NULL,
    is_locked integer
);


ALTER TABLE public.migrations_lock OWNER TO wikijs;

--
-- Name: migrations_lock_index_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public.migrations_lock_index_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.migrations_lock_index_seq OWNER TO wikijs;

--
-- Name: migrations_lock_index_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public.migrations_lock_index_seq OWNED BY public.migrations_lock.index;


--
-- Name: navigation; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.navigation (
    key character varying(255) NOT NULL,
    config json
);


ALTER TABLE public.navigation OWNER TO wikijs;

--
-- Name: pageHistory; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."pageHistory" (
    id integer NOT NULL,
    path character varying(255) NOT NULL,
    hash character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    "isPrivate" boolean DEFAULT false NOT NULL,
    "isPublished" boolean DEFAULT false NOT NULL,
    "publishStartDate" character varying(255),
    "publishEndDate" character varying(255),
    action character varying(255) DEFAULT 'updated'::character varying,
    "pageId" integer,
    content text,
    "contentType" character varying(255) NOT NULL,
    "createdAt" character varying(255) NOT NULL,
    "editorKey" character varying(255),
    "localeCode" character varying(5),
    "authorId" integer,
    "versionDate" character varying(255) DEFAULT ''::character varying NOT NULL,
    extra json DEFAULT '{}'::json NOT NULL
);


ALTER TABLE public."pageHistory" OWNER TO wikijs;

--
-- Name: pageHistoryTags; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."pageHistoryTags" (
    id integer NOT NULL,
    "pageId" integer,
    "tagId" integer
);


ALTER TABLE public."pageHistoryTags" OWNER TO wikijs;

--
-- Name: pageHistoryTags_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."pageHistoryTags_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."pageHistoryTags_id_seq" OWNER TO wikijs;

--
-- Name: pageHistoryTags_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."pageHistoryTags_id_seq" OWNED BY public."pageHistoryTags".id;


--
-- Name: pageHistory_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."pageHistory_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."pageHistory_id_seq" OWNER TO wikijs;

--
-- Name: pageHistory_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."pageHistory_id_seq" OWNED BY public."pageHistory".id;


--
-- Name: pageLinks; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."pageLinks" (
    id integer NOT NULL,
    path character varying(255) NOT NULL,
    "localeCode" character varying(5) NOT NULL,
    "pageId" integer
);


ALTER TABLE public."pageLinks" OWNER TO wikijs;

--
-- Name: pageLinks_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."pageLinks_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."pageLinks_id_seq" OWNER TO wikijs;

--
-- Name: pageLinks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."pageLinks_id_seq" OWNED BY public."pageLinks".id;


--
-- Name: pageTags; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."pageTags" (
    id integer NOT NULL,
    "pageId" integer,
    "tagId" integer
);


ALTER TABLE public."pageTags" OWNER TO wikijs;

--
-- Name: pageTags_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."pageTags_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."pageTags_id_seq" OWNER TO wikijs;

--
-- Name: pageTags_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."pageTags_id_seq" OWNED BY public."pageTags".id;


--
-- Name: pageTree; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."pageTree" (
    id integer NOT NULL,
    path character varying(255) NOT NULL,
    depth integer NOT NULL,
    title character varying(255) NOT NULL,
    "isPrivate" boolean DEFAULT false NOT NULL,
    "isFolder" boolean DEFAULT false NOT NULL,
    "privateNS" character varying(255),
    parent integer,
    "pageId" integer,
    "localeCode" character varying(5),
    ancestors json
);


ALTER TABLE public."pageTree" OWNER TO wikijs;

--
-- Name: pages; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.pages (
    id integer NOT NULL,
    path character varying(255) NOT NULL,
    hash character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    "isPrivate" boolean DEFAULT false NOT NULL,
    "isPublished" boolean DEFAULT false NOT NULL,
    "privateNS" character varying(255),
    "publishStartDate" character varying(255),
    "publishEndDate" character varying(255),
    content text,
    render text,
    toc json,
    "contentType" character varying(255) NOT NULL,
    "createdAt" character varying(255) NOT NULL,
    "updatedAt" character varying(255) NOT NULL,
    "editorKey" character varying(255),
    "localeCode" character varying(5),
    "authorId" integer,
    "creatorId" integer,
    extra json DEFAULT '{}'::json NOT NULL
);


ALTER TABLE public.pages OWNER TO wikijs;

--
-- Name: pagesVector; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."pagesVector" (
    id integer NOT NULL,
    path character varying(255),
    locale character varying(255),
    title character varying(255),
    description character varying(255),
    tokens tsvector,
    content text
);


ALTER TABLE public."pagesVector" OWNER TO wikijs;

--
-- Name: pagesVector_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."pagesVector_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."pagesVector_id_seq" OWNER TO wikijs;

--
-- Name: pagesVector_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."pagesVector_id_seq" OWNED BY public."pagesVector".id;


--
-- Name: pagesWords; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."pagesWords" (
    word text
);


ALTER TABLE public."pagesWords" OWNER TO wikijs;

--
-- Name: pages_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public.pages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pages_id_seq OWNER TO wikijs;

--
-- Name: pages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public.pages_id_seq OWNED BY public.pages.id;


--
-- Name: renderers; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.renderers (
    key character varying(255) NOT NULL,
    "isEnabled" boolean DEFAULT false NOT NULL,
    config json
);


ALTER TABLE public.renderers OWNER TO wikijs;

--
-- Name: searchEngines; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."searchEngines" (
    key character varying(255) NOT NULL,
    "isEnabled" boolean DEFAULT false NOT NULL,
    config json
);


ALTER TABLE public."searchEngines" OWNER TO wikijs;

--
-- Name: sessions; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.sessions (
    sid character varying(255) NOT NULL,
    sess json NOT NULL,
    expired timestamp with time zone NOT NULL
);


ALTER TABLE public.sessions OWNER TO wikijs;

--
-- Name: settings; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.settings (
    key character varying(255) NOT NULL,
    value json,
    "updatedAt" character varying(255) NOT NULL
);


ALTER TABLE public.settings OWNER TO wikijs;

--
-- Name: storage; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.storage (
    key character varying(255) NOT NULL,
    "isEnabled" boolean DEFAULT false NOT NULL,
    mode character varying(255) DEFAULT 'push'::character varying NOT NULL,
    config json,
    "syncInterval" character varying(255),
    state json
);


ALTER TABLE public.storage OWNER TO wikijs;

--
-- Name: tags; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.tags (
    id integer NOT NULL,
    tag character varying(255) NOT NULL,
    title character varying(255),
    "createdAt" character varying(255) NOT NULL,
    "updatedAt" character varying(255) NOT NULL
);


ALTER TABLE public.tags OWNER TO wikijs;

--
-- Name: tags_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public.tags_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tags_id_seq OWNER TO wikijs;

--
-- Name: tags_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public.tags_id_seq OWNED BY public.tags.id;


--
-- Name: userAvatars; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."userAvatars" (
    id integer NOT NULL,
    data bytea NOT NULL
);


ALTER TABLE public."userAvatars" OWNER TO wikijs;

--
-- Name: userGroups; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."userGroups" (
    id integer NOT NULL,
    "userId" integer,
    "groupId" integer
);


ALTER TABLE public."userGroups" OWNER TO wikijs;

--
-- Name: userGroups_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."userGroups_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."userGroups_id_seq" OWNER TO wikijs;

--
-- Name: userGroups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."userGroups_id_seq" OWNED BY public."userGroups".id;


--
-- Name: userKeys; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public."userKeys" (
    id integer NOT NULL,
    kind character varying(255) NOT NULL,
    token character varying(255) NOT NULL,
    "createdAt" character varying(255) NOT NULL,
    "validUntil" character varying(255) NOT NULL,
    "userId" integer
);


ALTER TABLE public."userKeys" OWNER TO wikijs;

--
-- Name: userKeys_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public."userKeys_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."userKeys_id_seq" OWNER TO wikijs;

--
-- Name: userKeys_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public."userKeys_id_seq" OWNED BY public."userKeys".id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: wikijs
--

CREATE TABLE public.users (
    id integer NOT NULL,
    email character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    "providerId" character varying(255),
    password character varying(255),
    "tfaIsActive" boolean DEFAULT false NOT NULL,
    "tfaSecret" character varying(255),
    "jobTitle" character varying(255) DEFAULT ''::character varying,
    location character varying(255) DEFAULT ''::character varying,
    "pictureUrl" character varying(255),
    timezone character varying(255) DEFAULT 'America/New_York'::character varying NOT NULL,
    "isSystem" boolean DEFAULT false NOT NULL,
    "isActive" boolean DEFAULT false NOT NULL,
    "isVerified" boolean DEFAULT false NOT NULL,
    "mustChangePwd" boolean DEFAULT false NOT NULL,
    "createdAt" character varying(255) NOT NULL,
    "updatedAt" character varying(255) NOT NULL,
    "providerKey" character varying(255) DEFAULT 'local'::character varying NOT NULL,
    "localeCode" character varying(5) DEFAULT 'en'::character varying NOT NULL,
    "defaultEditor" character varying(255) DEFAULT 'markdown'::character varying NOT NULL,
    "lastLoginAt" character varying(255),
    "dateFormat" character varying(255) DEFAULT ''::character varying NOT NULL,
    appearance character varying(255) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.users OWNER TO wikijs;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: wikijs
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO wikijs;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: wikijs
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: apiKeys id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."apiKeys" ALTER COLUMN id SET DEFAULT nextval('public."apiKeys_id_seq"'::regclass);


--
-- Name: assetFolders id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."assetFolders" ALTER COLUMN id SET DEFAULT nextval('public."assetFolders_id_seq"'::regclass);


--
-- Name: assets id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.assets ALTER COLUMN id SET DEFAULT nextval('public.assets_id_seq'::regclass);


--
-- Name: comments id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.comments ALTER COLUMN id SET DEFAULT nextval('public.comments_id_seq'::regclass);


--
-- Name: groups id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.groups ALTER COLUMN id SET DEFAULT nextval('public.groups_id_seq'::regclass);


--
-- Name: migrations id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.migrations ALTER COLUMN id SET DEFAULT nextval('public.migrations_id_seq'::regclass);


--
-- Name: migrations_lock index; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.migrations_lock ALTER COLUMN index SET DEFAULT nextval('public.migrations_lock_index_seq'::regclass);


--
-- Name: pageHistory id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistory" ALTER COLUMN id SET DEFAULT nextval('public."pageHistory_id_seq"'::regclass);


--
-- Name: pageHistoryTags id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistoryTags" ALTER COLUMN id SET DEFAULT nextval('public."pageHistoryTags_id_seq"'::regclass);


--
-- Name: pageLinks id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageLinks" ALTER COLUMN id SET DEFAULT nextval('public."pageLinks_id_seq"'::regclass);


--
-- Name: pageTags id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageTags" ALTER COLUMN id SET DEFAULT nextval('public."pageTags_id_seq"'::regclass);


--
-- Name: pages id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.pages ALTER COLUMN id SET DEFAULT nextval('public.pages_id_seq'::regclass);


--
-- Name: pagesVector id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pagesVector" ALTER COLUMN id SET DEFAULT nextval('public."pagesVector_id_seq"'::regclass);


--
-- Name: tags id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.tags ALTER COLUMN id SET DEFAULT nextval('public.tags_id_seq'::regclass);


--
-- Name: userGroups id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."userGroups" ALTER COLUMN id SET DEFAULT nextval('public."userGroups_id_seq"'::regclass);


--
-- Name: userKeys id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."userKeys" ALTER COLUMN id SET DEFAULT nextval('public."userKeys_id_seq"'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: analytics; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.analytics (key, "isEnabled", config) FROM stdin;
azureinsights	f	{"instrumentationKey":""}
baidutongji	f	{"propertyTrackingId":""}
countly	f	{"appKey":"","serverUrl":""}
elasticapm	f	{"serverUrl":"http://apm.example.com:8200","serviceName":"wiki-js","environment":""}
fathom	f	{"host":"","siteId":""}
fullstory	f	{"org":""}
google	f	{"propertyTrackingId":""}
gtm	f	{"containerTrackingId":""}
hotjar	f	{"siteId":""}
matomo	f	{"siteId":1,"serverHost":"https://example.matomo.cloud"}
newrelic	f	{"licenseKey":"","appId":"","beacon":"bam.nr-data.net","errorBeacon":"bam.nr-data.net"}
plausible	f	{"domain":"","plausibleJsSrc":"https://plausible.io/js/plausible.js"}
statcounter	f	{"projectId":"","securityToken":""}
yandex	f	{"tagNumber":""}
\.


--
-- Data for Name: apiKeys; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."apiKeys" (id, name, key, expiration, "isRevoked", "createdAt", "updatedAt") FROM stdin;
\.


--
-- Data for Name: assetData; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."assetData" (id, data) FROM stdin;
\.


--
-- Data for Name: assetFolders; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."assetFolders" (id, name, slug, "parentId") FROM stdin;
\.


--
-- Data for Name: assets; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.assets (id, filename, hash, ext, kind, mime, "fileSize", metadata, "createdAt", "updatedAt", "folderId", "authorId") FROM stdin;
\.


--
-- Data for Name: authentication; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.authentication (key, "isEnabled", config, "selfRegistration", "domainWhitelist", "autoEnrollGroups", "order", "strategyKey", "displayName") FROM stdin;
local	t	{}	f	{"v":[]}	{"v":[]}	0	local	Local
\.


--
-- Data for Name: brute; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.brute (key, "firstRequest", "lastRequest", lifetime, count) FROM stdin;
\.


--
-- Data for Name: commentProviders; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."commentProviders" (key, "isEnabled", config) FROM stdin;
commento	f	{"instanceUrl":"https://cdn.commento.io"}
default	t	{"akismet":"","minDelay":30}
disqus	f	{"accountName":""}
\.


--
-- Data for Name: comments; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.comments (id, content, "createdAt", "updatedAt", "pageId", "authorId", render, name, email, ip, "replyTo") FROM stdin;
\.


--
-- Data for Name: editors; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.editors (key, "isEnabled", config) FROM stdin;
api	f	{}
ckeditor	f	{}
code	f	{}
markdown	t	{}
redirect	f	{}
wysiwyg	f	{}
\.


--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.groups (id, name, permissions, "pageRules", "isSystem", "createdAt", "updatedAt", "redirectOnLogin") FROM stdin;
1	Administrators	["manage:system"]	[]	t	2022-04-12T06:51:27.362Z	2022-04-12T06:51:27.362Z	/
2	Guests	["read:pages","read:assets","read:comments"]	[{"id":"guest","roles":["read:pages","read:assets","read:comments"],"match":"START","deny":false,"path":"","locales":[]}]	t	2022-04-12T06:51:27.386Z	2022-04-12T06:51:27.386Z	/
\.


--
-- Data for Name: locales; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.locales (code, strings, "isRTL", name, "nativeName", availability, "createdAt", "updatedAt") FROM stdin;
en	{"common":{"footer":{"poweredBy":"Powered by","copyright":"© {{year}} {{company}}. All rights reserved.","license":"Content is available under the {{license}}, by {{company}}."},"actions":{"save":"Save","cancel":"Cancel","download":"Download","upload":"Upload","discard":"Discard","clear":"Clear","create":"Create","edit":"Edit","delete":"Delete","refresh":"Refresh","saveChanges":"Save Changes","proceed":"Proceed","ok":"OK","add":"Add","apply":"Apply","browse":"Browse...","close":"Close","page":"Page","discardChanges":"Discard Changes","move":"Move","rename":"Rename","optimize":"Optimize","preview":"Preview","properties":"Properties","insert":"Insert","fetch":"Fetch","generate":"Generate","confirm":"Confirm","copy":"Copy","returnToTop":"Return to top","exit":"Exit","select":"Select","convert":"Convert"},"newpage":{"title":"This page does not exist yet.","subtitle":"Would you like to create it now?","create":"Create Page","goback":"Go back"},"unauthorized":{"title":"Unauthorized","action":{"view":"You cannot view this page.","source":"You cannot view the page source.","history":"You cannot view the page history.","edit":"You cannot edit the page.","create":"You cannot create the page.","download":"You cannot download the page content.","downloadVersion":"You cannot download the content for this page version.","sourceVersion":"You cannot view the source of this version of the page."},"goback":"Go Back","login":"Login As..."},"notfound":{"gohome":"Home","title":"Not Found","subtitle":"This page does not exist."},"welcome":{"title":"Welcome to your wiki!","subtitle":"Let's get started and create the home page.","createhome":"Create Home Page","goadmin":"Administration"},"header":{"home":"Home","newPage":"New Page","currentPage":"Current Page","view":"View","edit":"Edit","history":"History","viewSource":"View Source","move":"Move / Rename","delete":"Delete","assets":"Assets","imagesFiles":"Images & Files","search":"Search...","admin":"Administration","account":"Account","myWiki":"My Wiki","profile":"Profile","logout":"Logout","login":"Login","searchHint":"Type at least 2 characters to start searching...","searchLoading":"Searching...","searchNoResult":"No pages matching your query.","searchResultsCount":"Found {{total}} results","searchDidYouMean":"Did you mean...","searchClose":"Close","searchCopyLink":"Copy Search Link","language":"Language","browseTags":"Browse by Tags","siteMap":"Site Map","pageActions":"Page Actions","duplicate":"Duplicate","convert":"Convert"},"page":{"lastEditedBy":"Last edited by","unpublished":"Unpublished","editPage":"Edit Page","toc":"Page Contents","bookmark":"Bookmark","share":"Share","printFormat":"Print Format","delete":"Delete Page","deleteTitle":"Are you sure you want to delete page {{title}}?","deleteSubtitle":"The page can be restored from the administration area.","viewingSource":"Viewing source of page {{path}}","returnNormalView":"Return to Normal View","id":"ID {{id}}","published":"Published","private":"Private","global":"Global","loading":"Loading Page...","viewingSourceVersion":"Viewing source as of {{date}} of page {{path}}","versionId":"Version ID {{id}}","unpublishedWarning":"This page is not published.","tags":"Tags","tagsMatching":"Pages matching tags","convert":"Convert Page","convertTitle":"Select the editor you want to use going forward for the page {{title}}:","convertSubtitle":"The page content will be converted into the format of the newly selected editor. Note that some formatting or non-rendered content may be lost as a result of the conversion. A snapshot will be added to the page history and can be restored at any time."},"error":{"unexpected":"An unexpected error occurred."},"password":{"veryWeak":"Very Weak","weak":"Weak","average":"Average","strong":"Strong","veryStrong":"Very Strong"},"user":{"search":"Search User","searchPlaceholder":"Search Users..."},"duration":{"every":"Every","minutes":"Minute(s)","hours":"Hour(s)","days":"Day(s)","months":"Month(s)","years":"Year(s)"},"outdatedBrowserWarning":"Your browser is outdated. Upgrade to a {{modernBrowser}}.","modernBrowser":"modern browser","license":{"none":"None","ccby":" Creative Commons Attribution License","ccbysa":"Creative Commons Attribution-ShareAlike License","ccbynd":"Creative Commons Attribution-NoDerivs License","ccbync":"Creative Commons Attribution-NonCommercial License","ccbyncsa":"Creative Commons Attribution-NonCommercial-ShareAlike License","ccbyncnd":"Creative Commons Attribution-NonCommercial-NoDerivs License","cc0":"Public Domain","alr":"All Rights Reserved"},"sidebar":{"browse":"Browse","mainMenu":"Main Menu","currentDirectory":"Current Directory","root":"(root)"},"comments":{"title":"Comments","newPlaceholder":"Write a new comment...","fieldName":"Your Name","fieldEmail":"Your Email Address","markdownFormat":"Markdown Format","postComment":"Post Comment","loading":"Loading comments...","postingAs":"Posting as {{name}}","beFirst":"Be the first to comment.","none":"No comments yet.","updateComment":"Update Comment","deleteConfirmTitle":"Confirm Delete","deleteWarn":"Are you sure you want to permanently delete this comment?","deletePermanentWarn":"This action cannot be undone!","modified":"modified {{reldate}}","postSuccess":"New comment posted successfully.","contentMissingError":"Comment is empty or too short!","updateSuccess":"Comment was updated successfully.","deleteSuccess":"Comment was deleted successfully.","viewDiscussion":"View Discussion","newComment":"New Comment","fieldContent":"Comment Content","sdTitle":"Talk"},"pageSelector":{"createTitle":"Select New Page Location","moveTitle":"Move / Rename Page Location","selectTitle":"Select a Page","virtualFolders":"Virtual Folders","pages":"Pages","folderEmptyWarning":"This folder is empty."}},"auth":{"loginRequired":"Login required","fields":{"emailUser":"Email / Username","password":"Password","email":"Email Address","verifyPassword":"Verify Password","name":"Name","username":"Username"},"actions":{"login":"Log In","register":"Register"},"errors":{"invalidLogin":"Invalid Login","invalidLoginMsg":"The email or password is invalid.","invalidUserEmail":"Invalid User Email","loginError":"Login error","notYetAuthorized":"You have not been authorized to login to this site yet.","tooManyAttempts":"Too many attempts!","tooManyAttemptsMsg":"You've made too many failed attempts in a short period of time, please try again {{time}}.","userNotFound":"User not found"},"providers":{"local":"Local","windowslive":"Microsoft Account","azure":"Azure Active Directory","google":"Google ID","facebook":"Facebook","github":"GitHub","slack":"Slack","ldap":"LDAP / Active Directory"},"tfa":{"title":"Two Factor Authentication","subtitle":"Security code required:","placeholder":"XXXXXX","verifyToken":"Verify"},"registerTitle":"Create an account","switchToLogin":{"text":"Already have an account? {{link}}","link":"Login instead"},"loginUsingStrategy":"Login using {{strategy}}","forgotPasswordLink":"Forgot your password?","orLoginUsingStrategy":"or login using...","switchToRegister":{"text":"Don't have an account yet? {{link}}","link":"Create an account"},"invalidEmailUsername":"Enter a valid email / username.","invalidPassword":"Enter a valid password.","loginSuccess":"Login Successful! Redirecting...","signingIn":"Signing In...","genericError":"Authentication is unavailable.","registerSubTitle":"Fill-in the form below to create your account.","pleaseWait":"Please wait","registerSuccess":"Account created successfully!","registering":"Creating account...","missingEmail":"Missing email address.","invalidEmail":"Email address is invalid.","missingPassword":"Missing password.","passwordTooShort":"Password is too short.","passwordNotMatch":"Both passwords do not match.","missingName":"Name is missing.","nameTooShort":"Name is too short.","nameTooLong":"Name is too long.","forgotPasswordCancel":"Cancel","sendResetPassword":"Reset Password","forgotPasswordSubtitle":"Enter your email address to receive the instructions to reset your password:","registerCheckEmail":"Check your emails to activate your account.","changePwd":{"subtitle":"Choose a new password","instructions":"You must choose a new password:","newPasswordPlaceholder":"New Password","newPasswordVerifyPlaceholder":"Verify New Password","proceed":"Change Password","loading":"Changing password..."},"forgotPasswordLoading":"Requesting password reset...","forgotPasswordSuccess":"Check your emails for password reset instructions!","selectAuthProvider":"Select Authentication Provider","enterCredentials":"Enter your credentials","forgotPasswordTitle":"Forgot your password","tfaFormTitle":"Enter the security code generated from your trusted device:","tfaSetupTitle":"Your administrator has required Two-Factor Authentication (2FA) to be enabled on your account.","tfaSetupInstrFirst":"1) Scan the QR code below from your mobile 2FA application:","tfaSetupInstrSecond":"2) Enter the security code generated from your trusted device:"},"admin":{"dashboard":{"title":"Dashboard","subtitle":"Wiki.js","pages":"Pages","users":"Users","groups":"Groups","versionLatest":"You are running the latest version.","versionNew":"A new version is available: {{version}}","contributeSubtitle":"Wiki.js is a free and open source project. There are several ways you can contribute to the project.","contributeHelp":"We need your help!","contributeLearnMore":"Learn More","recentPages":"Recent Pages","mostPopularPages":"Most Popular Pages","lastLogins":"Last Logins"},"general":{"title":"General","subtitle":"Main settings of your wiki","siteInfo":"Site Info","siteBranding":"Site Branding","general":"General","siteUrl":"Site URL","siteUrlHint":"Full URL to your wiki, without the trailing slash. (e.g. https://wiki.example.com)","siteTitle":"Site Title","siteTitleHint":"Displayed in the top bar and appended to all pages meta title.","logo":"Logo","uploadLogo":"Upload Logo","uploadClear":"Clear","uploadSizeHint":"An image of {{size}} pixels is recommended for best results.","uploadTypesHint":"{{typeList}} or {{lastType}} files only","footerCopyright":"Footer Copyright","companyName":"Company / Organization Name","companyNameHint":"Name to use when displaying copyright notice in the footer. Leave empty to hide.","siteDescription":"Site Description","siteDescriptionHint":"Default description when none is provided for a page.","metaRobots":"Meta Robots","metaRobotsHint":"Default: Index, Follow. Can also be set on a per-page basis.","logoUrl":"Logo URL","logoUrlHint":"Specify an image to use as the logo. SVG, PNG, JPG are supported, in a square ratio, 34x34 pixels or larger. Click the button on the right to upload a new image.","contentLicense":"Content License","contentLicenseHint":"License shown in the footer of all content pages.","siteTitleInvalidChars":"Site Title contains invalid characters.","saveSuccess":"Site configuration saved successfully."},"locale":{"title":"Locale","subtitle":"Set localization options for your wiki","settings":"Locale Settings","namespacing":"Multilingual Namespacing","downloadTitle":"Download Locale","base":{"labelWithNS":"Base Locale","hint":"All UI text elements will be displayed in selected language.","label":"Site Locale"},"autoUpdate":{"label":"Update Automatically","hintWithNS":"Automatically download updates to all namespaced locales enabled below.","hint":"Automatically download updates to this locale as they become available."},"namespaces":{"label":"Multilingual Namespaces","hint":"Enables multiple language versions of the same page."},"activeNamespaces":{"label":"Active Namespaces","hint":"List of locales enabled for multilingual namespacing. The base locale defined above will always be included regardless of this selection."},"namespacingPrefixWarning":{"title":"The locale code will be prefixed to all paths. (e.g. /{{langCode}}/page-name)","subtitle":"Paths without a locale code will be automatically redirected to the base locale defined above."},"sideload":"Sideload Locale Package","sideloadHelp":"If you are not connected to the internet or cannot download locale files using the method above, you can instead sideload packages manually by uploading them below.","code":"Code","name":"Name","nativeName":"Native Name","rtl":"RTL","availability":"Availability","download":"Download"},"stats":{"title":"Statistics"},"theme":{"title":"Theme","subtitle":"Modify the look & feel of your wiki","siteTheme":"Site Theme","siteThemeHint":"Themes affect how content pages are displayed. Other site sections (such as the editor or admin area) are not affected.","darkMode":"Dark Mode","darkModeHint":"Not recommended for accessibility. May not be supported by all themes.","codeInjection":"Code Injection","cssOverride":"CSS Override","cssOverrideHint":"CSS code to inject after system default CSS. Consider using custom themes if you have a large amount of css code. Injecting too much CSS code will result in poor page load performance! CSS will automatically be minified.","headHtmlInjection":"Head HTML Injection","headHtmlInjectionHint":"HTML code to be injected just before the closing head tag. Usually for script tags.","bodyHtmlInjection":"Body HTML Injection","bodyHtmlInjectionHint":"HTML code to be injected just before the closing body tag.","downloadThemes":"Download Themes","iconset":"Icon Set","iconsetHint":"Set of icons to use for the sidebar navigation.","downloadName":"Name","downloadAuthor":"Author","downloadDownload":"Download","cssOverrideWarning":"{{caution}} When adding styles for page content, you must scope them to the {{cssClass}} class. Omitting this could break the layout of the editor!","cssOverrideWarningCaution":"CAUTION:","options":"Theme Options"},"groups":{"title":"Groups"},"users":{"title":"Users","active":"Active","inactive":"Inactive","verified":"Verified","unverified":"Unverified","edit":"Edit User","id":"ID {{id}}","basicInfo":"Basic Info","email":"Email","displayName":"Display Name","authentication":"Authentication","authProvider":"Provider","password":"Password","changePassword":"Change Password","newPassword":"New Password","tfa":"Two Factor Authentication (2FA)","toggle2FA":"Toggle 2FA","authProviderId":"Provider Id","groups":"User Groups","noGroupAssigned":"This user is not assigned to any group yet. You must assign at least 1 group to a user.","selectGroup":"Select Group...","groupAssign":"Assign","extendedMetadata":"Extended Metadata","location":"Location","jobTitle":"Job Title","timezone":"Timezone","userUpdateSuccess":"User updated successfully.","userAlreadyAssignedToGroup":"User is already assigned to this group!","deleteConfirmTitle":"Delete User?","deleteConfirmText":"Are you sure you want to delete user {{username}}?","updateUser":"Update User","groupAssignNotice":"Note that you cannot assign users to the Administrators or Guests groups from this panel.","deleteConfirmForeignNotice":"Note that you cannot delete a user that already created content. You must instead either deactivate the user or delete all content that was created by that user.","userVerifySuccess":"User has been verified successfully.","userActivateSuccess":"User has been activated successfully.","userDeactivateSuccess":"User deactivated successfully.","deleteConfirmReplaceWarn":"Any content (pages, uploads, comments, etc.) that was created by this user will be reassigned to the user selected below. It is recommended to create a dummy target user (e.g. Deleted User) if you don't want the content to be reassigned to any current active user.","userTFADisableSuccess":"2FA was disabled successfully.","userTFAEnableSuccess":"2FA was enabled successfully."},"auth":{"title":"Authentication","subtitle":"Configure the authentication settings of your wiki","strategies":"Strategies","globalAdvSettings":"Global Advanced Settings","jwtAudience":"JWT Audience","jwtAudienceHint":"Audience URN used in JWT issued upon login. Usually your domain name. (e.g. urn:your.domain.com)","tokenExpiration":"Token Expiration","tokenExpirationHint":"The expiration period of a token until it must be renewed. (default: 30m)","tokenRenewalPeriod":"Token Renewal Period","tokenRenewalPeriodHint":"The maximum period a token can be renewed when expired. (default: 14d)","strategyState":"This strategy is {{state}} {{locked}}","strategyStateActive":"active","strategyStateInactive":"not active","strategyStateLocked":"and cannot be disabled.","strategyConfiguration":"Strategy Configuration","strategyNoConfiguration":"This strategy has no configuration options you can modify.","registration":"Registration","selfRegistration":"Allow self-registration","selfRegistrationHint":"Allow any user successfully authorized by the strategy to access the wiki.","domainsWhitelist":"Limit to specific email domains","domainsWhitelistHint":"A list of domains authorized to register. The user email address domain must match one of these to gain access.","autoEnrollGroups":"Assign to group","autoEnrollGroupsHint":"Automatically assign new users to these groups.","security":"Security","force2fa":"Force all users to use Two-Factor Authentication (2FA)","force2faHint":"Users will be required to setup 2FA the first time they login and cannot be disabled by the user.","configReference":"Configuration Reference","configReferenceSubtitle":"Some strategies may require some configuration values to be set on your provider. These are provided for reference only and may not be needed by the current strategy.","siteUrlNotSetup":"You must set a valid {{siteUrl}} first! Click on {{general}} in the left sidebar.","allowedWebOrigins":"Allowed Web Origins","callbackUrl":"Callback URL / Redirect URI","loginUrl":"Login URL","logoutUrl":"Logout URL","tokenEndpointAuthMethod":"Token Endpoint Authentication Method","refreshSuccess":"List of strategies has been refreshed.","saveSuccess":"Authentication configuration saved successfully.","activeStrategies":"Active Strategies","addStrategy":"Add Strategy","strategyIsEnabled":"Active","strategyIsEnabledHint":"Are users able to login using this strategy?","displayName":"Display Name","displayNameHint":"The title shown to the end user for this authentication strategy."},"editor":{"title":"Editor"},"logging":{"title":"Logging"},"rendering":{"title":"Rendering","subtitle":"Configure the page rendering pipeline"},"search":{"title":"Search Engine","subtitle":"Configure the search capabilities of your wiki","rebuildIndex":"Rebuild Index","searchEngine":"Search Engine","engineConfig":"Engine Configuration","engineNoConfig":"This engine has no configuration options you can modify.","listRefreshSuccess":"List of search engines has been refreshed.","configSaveSuccess":"Search engine configuration saved successfully.","indexRebuildSuccess":"Index rebuilt successfully."},"storage":{"title":"Storage","subtitle":"Set backup and sync targets for your content","targets":"Targets","status":"Status","lastSync":"Last synchronization {{time}}","lastSyncAttempt":"Last attempt was {{time}}","errorMsg":"Error Message","noTarget":"You don't have any active storage target.","targetConfig":"Target Configuration","noConfigOption":"This storage target has no configuration options you can modify.","syncDirection":"Sync Direction","syncDirectionSubtitle":"Choose how content synchronization is handled for this storage target.","syncDirBi":"Bi-directional","syncDirPush":"Push to target","syncDirPull":"Pull from target","unsupported":"Unsupported","syncDirBiHint":"In bi-directional mode, content is first pulled from the storage target. Any newer content overwrites local content. New content since last sync is then pushed to the storage target, overwriting any content on target if present.","syncDirPushHint":"Content is always pushed to the storage target, overwriting any existing content. This is safest choice for backup scenarios.","syncDirPullHint":"Content is always pulled from the storage target, overwriting any local content which already exists. This choice is usually reserved for single-use content import. Caution with this option as any local content will always be overwritten!","syncSchedule":"Sync Schedule","syncScheduleHint":"For performance reasons, this storage target synchronize changes on an interval-based schedule, instead of on every change. Define at which interval should the synchronization occur.","syncScheduleCurrent":"Currently set to every {{schedule}}.","syncScheduleDefault":"The default is every {{schedule}}.","actions":"Actions","actionRun":"Run","targetState":"This storage target is {{state}}","targetStateActive":"active","targetStateInactive":"inactive","actionsInactiveWarn":"You must enable this storage target and apply changes before you can run actions."},"api":{"title":"API Access","subtitle":"Manage keys to access the API","enabled":"API Enabled","disabled":"API Disabled","enableButton":"Enable API","disableButton":"Disable API","newKeyButton":"New API Key","headerName":"Name","headerKeyEnding":"Key Ending","headerExpiration":"Expiration","headerCreated":"Created","headerLastUpdated":"Last Updated","headerRevoke":"Revoke","noKeyInfo":"No API keys have been generated yet.","revokeConfirm":"Revoke API Key?","revokeConfirmText":"Are you sure you want to revoke key {{name}}? This action cannot be undone!","revoke":"Revoke","refreshSuccess":"List of API keys has been refreshed.","revokeSuccess":"The key has been revoked successfully.","newKeyTitle":"New API Key","newKeySuccess":"API key created successfully.","newKeyNameError":"Name is missing or invalid.","newKeyGroupError":"You must select a group.","newKeyGuestGroupError":"The guests group cannot be used for API keys.","newKeyNameHint":"Purpose of this key","newKeyName":"Name","newKeyExpiration":"Expiration","newKeyExpirationHint":"You can still revoke a key anytime regardless of the expiration.","newKeyPermissionScopes":"Permission Scopes","newKeyFullAccess":"Full Access","newKeyGroupPermissions":"or use group permissions...","newKeyGroup":"Group","newKeyGroupHint":"The API key will have the same permissions as the selected group.","expiration30d":"30 days","expiration90d":"90 days","expiration180d":"180 days","expiration1y":"1 year","expiration3y":"3 years","newKeyCopyWarn":"Copy the key shown below as {{bold}}","newKeyCopyWarnBold":"it will NOT be shown again","toggleStateEnabledSuccess":"API has been enabled successfully.","toggleStateDisabledSuccess":"API has been disabled successfully."},"system":{"title":"System Info","subtitle":"Information about your system","hostInfo":"Host Information","currentVersion":"Current Version","latestVersion":"Latest Version","published":"Published","os":"Operating System","hostname":"Hostname","cpuCores":"CPU Cores","totalRAM":"Total RAM","workingDirectory":"Working Directory","configFile":"Configuration File","ramUsage":"RAM Usage: {{used}} / {{total}}","dbPartialSupport":"Your database version is not fully supported. Some functionality may be limited or not work as expected.","refreshSuccess":"System Info has been refreshed."},"utilities":{"title":"Utilities","subtitle":"Maintenance and miscellaneous tools","tools":"Tools","authTitle":"Authentication","authSubtitle":"Various tools for authentication / users","cacheTitle":"Flush Cache","cacheSubtitle":"Flush cache of various components","graphEndpointTitle":"GraphQL Endpoint","graphEndpointSubtitle":"Change the GraphQL endpoint for Wiki.js","importv1Title":"Import from Wiki.js 1.x","importv1Subtitle":"Migrate data from a previous 1.x installation","telemetryTitle":"Telemetry","telemetrySubtitle":"Enable/Disable telemetry or reset the client ID","contentTitle":"Content","contentSubtitle":"Various tools for pages"},"dev":{"title":"Developer Tools","flags":{"title":"Flags"},"graphiql":{"title":"GraphiQL"},"voyager":{"title":"Voyager"}},"contribute":{"title":"Contribute to Wiki.js","subtitle":"Help support Wiki.js development and operations","fundOurWork":"Fund our work","spreadTheWord":"Spread the word","talkToFriends":"Talk to your friends and colleagues about how awesome Wiki.js is!","followUsOnTwitter":"Follow us on {{0}}.","submitAnIdea":"Submit an idea or vote on a proposed one on the {{0}}.","submitAnIdeaLink":"feature requests board","foundABug":"Found a bug? Submit an issue on {{0}}.","helpTranslate":"Help translate Wiki.js in your language. Let us know on {{0}}.","makeADonation":"Make a donation","contribute":"Contribute","openCollective":"Wiki.js is also part of the Open Collective initiative, a transparent fund that goes toward community resources. You can contribute financially by making a monthly or one-time donation:","needYourHelp":"We need your help to keep improving the software and run the various associated services (e.g. hosting and networking).","openSource":"Wiki.js is a free and open-source software brought to you with {{0}} by {{1}} and {{2}}.","openSourceContributors":"contributors","tshirts":"You can also buy Wiki.js t-shirts to support the project financially:","shop":"Wiki.js Shop","becomeAPatron":"Become a Patron","patreon":"Become a backer or sponsor via Patreon (goes directly into supporting lead developer Nicolas Giard's goal of working full-time on Wiki.js)","paypal":"Make a one-time or recurring donation via Paypal:","ethereum":"We accept donations using Ethereum:","github":"Become a sponsor via GitHub Sponsors (goes directly into supporting lead developer Nicolas Giard's goal of working full-time on Wiki.js)","becomeASponsor":"Become a Sponsor"},"nav":{"site":"Site","users":"Users","modules":"Modules","system":"System"},"pages":{"title":"Pages"},"navigation":{"title":"Navigation","subtitle":"Manage the site navigation","link":"Link","divider":"Divider","header":"Header","label":"Label","icon":"Icon","targetType":"Target Type","target":"Target","noSelectionText":"Select a navigation item on the left.","untitled":"Untitled {{kind}}","navType":{"external":"External Link","home":"Home","page":"Page","searchQuery":"Search Query","externalblank":"External Link (New Window)"},"edit":"Edit {{kind}}","delete":"Delete {{kind}}","saveSuccess":"Navigation saved successfully.","noItemsText":"Click the Add button to add your first navigation item.","emptyList":"Navigation is empty","visibilityMode":{"all":"Visible to everyone","restricted":"Visible to select groups..."},"selectPageButton":"Select Page...","mode":"Navigation Mode","modeSiteTree":{"title":"Site Tree","description":"Classic Tree-based Navigation"},"modeCustom":{"title":"Custom Navigation","description":"Static Navigation Menu + Site Tree Button"},"modeNone":{"title":"None","description":"Disable Site Navigation"},"copyFromLocale":"Copy from locale...","sourceLocale":"Source Locale","sourceLocaleHint":"The locale from which navigation items will be copied from.","copyFromLocaleInfoText":"Select the locale from which items will be copied from. Items will be appended to the current list of items in the active locale.","modeStatic":{"title":"Static Navigation","description":"Static Navigation Menu Only"}},"mail":{"title":"Mail","subtitle":"Configure mail settings","configuration":"Configuration","dkim":"DKIM (optional)","test":"Send a test email","testRecipient":"Recipient Email Address","testSend":"Send Email","sender":"Sender","senderName":"Sender Name","senderEmail":"Sender Email","smtp":"SMTP Settings","smtpHost":"Host","smtpPort":"Port","smtpPortHint":"Usually 465 (recommended), 587 or 25.","smtpTLS":"Secure (TLS)","smtpTLSHint":"Should be enabled when using port 465, otherwise turned off (587 or 25).","smtpUser":"Username","smtpPwd":"Password","dkimHint":"DKIM (DomainKeys Identified Mail) provides a layer of security on all emails sent from Wiki.js by providing the means for recipients to validate the domain name and ensure the message authenticity.","dkimUse":"Use DKIM","dkimDomainName":"Domain Name","dkimKeySelector":"Key Selector","dkimPrivateKey":"Private Key","dkimPrivateKeyHint":"Private key for the selector in PEM format","testHint":"Send a test email to ensure your SMTP configuration is working.","saveSuccess":"Configuration saved successfully.","sendTestSuccess":"A test email was sent successfully.","smtpVerifySSL":"Verify SSL Certificate","smtpVerifySSLHint":"Some hosts requires SSL certificate checking to be disabled. Leave enabled for proper security."},"webhooks":{"title":"Webhooks","subtitle":"Manage webhooks to external services"},"adminArea":"Administration Area","analytics":{"title":"Analytics","subtitle":"Add analytics and tracking tools to your wiki","providers":"Providers","providerConfiguration":"Provider Configuration","providerNoConfiguration":"This provider has no configuration options you can modify.","refreshSuccess":"List of providers refreshed successfully.","saveSuccess":"Analytics configuration saved successfully"},"comments":{"title":"Comments","provider":"Provider","subtitle":"Add discussions to your wiki pages","providerConfig":"Provider Configuration","providerNoConfig":"This provider has no configuration options you can modify."},"tags":{"title":"Tags","subtitle":"Manage page tags","emptyList":"No tags to display.","edit":"Edit Tag","tag":"Tag","label":"Label","date":"Created {{created}} and last updated {{updated}}.","delete":"Delete this tag","noSelectionText":"Select a tag from the list on the left.","noItemsText":"Add a tag to a page to get started.","refreshSuccess":"Tags have been refreshed.","deleteSuccess":"Tag deleted successfully.","saveSuccess":"Tag has been saved successfully.","filter":"Filter...","viewLinkedPages":"View Linked Pages","deleteConfirm":"Delete Tag?","deleteConfirmText":"Are you sure you want to delete tag {{tag}}? The tag will also be unlinked from all pages."},"ssl":{"title":"SSL","subtitle":"Manage SSL configuration","provider":"Provider","providerHint":"Select Custom Certificate if you have your own certificate already.","domain":"Domain","domainHint":"Enter the fully qualified domain pointing to your wiki. (e.g. wiki.example.com)","providerOptions":"Provider Options","providerDisabled":"Disabled","providerLetsEncrypt":"Let's Encrypt","providerCustomCertificate":"Custom Certificate","ports":"Ports","httpPort":"HTTP Port","httpPortHint":"Non-SSL port the server will listen to for HTTP requests. Usually 80 or 3000.","httpsPort":"HTTPS Port","httpsPortHint":"SSL port the server will listen to for HTTPS requests. Usually 443.","httpPortRedirect":"Redirect HTTP requests to HTTPS","httpPortRedirectHint":"Will automatically redirect any requests on the HTTP port to HTTPS.","writableConfigFileWarning":"Note that your config file must be writable in order to persist ports configuration.","renewCertificate":"Renew Certificate","status":"Certificate Status","expiration":"Certificate Expiration","subscriberEmail":"Subscriber Email","currentState":"Current State","httpPortRedirectTurnOn":"Turn On","httpPortRedirectTurnOff":"Turn Off","renewCertificateLoadingTitle":"Renewing Certificate...","renewCertificateLoadingSubtitle":"Do not leave this page.","renewCertificateSuccess":"Certificate renewed successfully.","httpPortRedirectSaveSuccess":"HTTP Redirection changed successfully."},"security":{"title":"Security","maxUploadSize":"Max Upload Size","maxUploadBatch":"Max Files per Upload","maxUploadBatchHint":"How many files can be uploaded in a single batch?","maxUploadSizeHint":"The maximum size for a single file.","maxUploadSizeSuffix":"bytes","maxUploadBatchSuffix":"files","uploads":"Uploads","uploadsInfo":"These settings only affect Wiki.js. If you're using a reverse-proxy (e.g. nginx, apache, Cloudflare), you must also change its settings to match.","subtitle":"Configure security settings","login":"Login","loginScreen":"Login Screen","jwt":"JWT Configuration","bypassLogin":"Bypass Login Screen","bypassLoginHint":"Should the user be redirected automatically to the first authentication provider.","loginBgUrl":"Login Background Image URL","loginBgUrlHint":"Specify an image to use as the login background. PNG and JPG are supported, 1920x1080 recommended. Leave empty for default. Click the button on the right to upload a new image. Note that the Guests group must have read-access to the selected image!","hideLocalLogin":"Hide Local Authentication Provider","hideLocalLoginHint":"Don't show the local authentication provider on the login screen. Add ?all to the URL to temporarily use it.","loginSecurity":"Security","enforce2fa":"Enforce 2FA","enforce2faHint":"Force all users to use Two-Factor Authentication when using an authentication provider with a user / password form."},"extensions":{"title":"Extensions","subtitle":"Install extensions for extra functionality"}},"editor":{"page":"Page","save":{"processing":"Rendering","pleaseWait":"Please wait...","createSuccess":"Page created successfully.","error":"An error occurred while creating the page","updateSuccess":"Page updated successfully.","saved":"Saved"},"props":{"pageProperties":"Page Properties","pageInfo":"Page Info","title":"Title","shortDescription":"Short Description","shortDescriptionHint":"Shown below the title","pathCategorization":"Path & Categorization","locale":"Locale","path":"Path","pathHint":"Do not include any leading or trailing slashes.","tags":"Tags","tagsHint":"Use tags to categorize your pages and make them easier to find.","publishState":"Publishing State","publishToggle":"Published","publishToggleHint":"Unpublished pages are still visible to users with write permissions on this page.","publishStart":"Publish starting on...","publishStartHint":"Leave empty for no start date","publishEnd":"Publish ending on...","publishEndHint":"Leave empty for no end date","info":"Info","scheduling":"Scheduling","social":"Social","categorization":"Categorization","socialFeatures":"Social Features","allowComments":"Allow Comments","allowCommentsHint":"Enable commenting abilities on this page.","allowRatings":"Allow Ratings","displayAuthor":"Display Author Info","displaySharingBar":"Display Sharing Toolbar","displaySharingBarHint":"Show a toolbar with buttons to share and print this page","displayAuthorHint":"Show the page author along with the last edition time.","allowRatingsHint":"Enable rating capabilities on this page.","scripts":"Scripts","css":"CSS","cssHint":"CSS will automatically be minified upon saving. Do not include surrounding style tags, only the actual CSS code.","styles":"Styles","html":"HTML","htmlHint":"You must surround your javascript code with HTML script tags."},"unsaved":{"title":"Discard Unsaved Changes?","body":"You have unsaved changes. Are you sure you want to leave the editor and discard any modifications you made since the last save?"},"select":{"title":"Which editor do you want to use for this page?","cannotChange":"This cannot be changed once the page is created.","customView":"or create a custom view?"},"assets":{"title":"Assets","newFolder":"New Folder","folderName":"Folder Name","folderNameNamingRules":"Must follow the asset folder {{namingRules}}.","folderNameNamingRulesLink":"naming rules","folderEmpty":"This asset folder is empty.","fileCount":"{{count}} files","headerId":"ID","headerFilename":"Filename","headerType":"Type","headerFileSize":"File Size","headerAdded":"Added","headerActions":"Actions","uploadAssets":"Upload Assets","uploadAssetsDropZone":"Browse or Drop files here...","fetchImage":"Fetch Remote Image","imageAlign":"Image Alignment","renameAsset":"Rename Asset","renameAssetSubtitle":"Enter the new name for this asset:","deleteAsset":"Delete Asset","deleteAssetConfirm":"Are you sure you want to delete asset","deleteAssetWarn":"This action cannot be undone!","refreshSuccess":"List of assets refreshed successfully.","uploadFailed":"File upload failed.","folderCreateSuccess":"Asset folder created successfully.","renameSuccess":"Asset renamed successfully.","deleteSuccess":"Asset deleted successfully.","noUploadError":"You must choose a file to upload first!"},"backToEditor":"Back to Editor","markup":{"bold":"Bold","italic":"Italic","strikethrough":"Strikethrough","heading":"Heading {{level}}","subscript":"Subscript","superscript":"Superscript","blockquote":"Blockquote","blockquoteInfo":"Info Blockquote","blockquoteSuccess":"Success Blockquote","blockquoteWarning":"Warning Blockquote","blockquoteError":"Error Blockquote","unorderedList":"Unordered List","orderedList":"Ordered List","inlineCode":"Inline Code","keyboardKey":"Keyboard Key","horizontalBar":"Horizontal Bar","togglePreviewPane":"Hide / Show Preview Pane","insertLink":"Insert Link","insertAssets":"Insert Assets","insertBlock":"Insert Block","insertCodeBlock":"Insert Code Block","insertVideoAudio":"Insert Video / Audio","insertDiagram":"Insert Diagram","insertMathExpression":"Insert Math Expression","tableHelper":"Table Helper","distractionFreeMode":"Distraction Free Mode","markdownFormattingHelp":"Markdown Formatting Help","noSelectionError":"Text must be selected first!","toggleSpellcheck":"Toggle Spellcheck"},"ckeditor":{"stats":"{{chars}} chars, {{words}} words"},"conflict":{"title":"Resolve Save Conflict","useLocal":"Use Local","useRemote":"Use Remote","useRemoteHint":"Discard local changes and use latest version","useLocalHint":"Use content in the left panel","viewLatestVersion":"View Latest Version","infoGeneric":"A more recent version of this page was saved by {{authorName}}, {{date}}","whatToDo":"What do you want to do?","whatToDoLocal":"Use your current local version and ignore the latest changes.","whatToDoRemote":"Use the remote version (latest) and discard your changes.","overwrite":{"title":"Overwrite with Remote Version?","description":"Are you sure you want to replace your current version with the latest remote content? {{refEditsLost}}","editsLost":"Your current edits will be lost."},"localVersion":"Local Version {{refEditable}}","editable":"(editable)","readonly":"(read-only)","remoteVersion":"Remote Version {{refReadOnly}}","leftPanelInfo":"Your current edit, based on page version from {{date}}","rightPanelInfo":"Last edited by {{authorName}}, {{date}}","pageTitle":"Title:","pageDescription":"Description:","warning":"Save conflict! Another user has already modified this page."},"unsavedWarning":"You have unsaved edits. Are you sure you want to leave the editor?"},"tags":{"currentSelection":"Current Selection","clearSelection":"Clear Selection","selectOneMoreTags":"Select one or more tags","searchWithinResultsPlaceholder":"Search within results...","locale":"Locale","orderBy":"Order By","selectOneMoreTagsHint":"Select one or more tags on the left.","retrievingResultsLoading":"Retrieving page results...","noResults":"Couldn't find any page with the selected tags.","noResultsWithFilter":"Couldn't find any page matching the current filtering options.","pageLastUpdated":"Last Updated {{date}}","orderByField":{"creationDate":"Creation Date","ID":"ID","lastModified":"Last Modified","path":"Path","title":"Title"},"localeAny":"Any"},"history":{"restore":{"confirmTitle":"Restore page version?","confirmText":"Are you sure you want to restore this page content as it was on {{date}}? This version will be copied on top of the current history. As such, newer versions will still be preserved.","confirmButton":"Restore","success":"Page version restored succesfully!"}},"profile":{"displayName":"Display Name","location":"Location","jobTitle":"Job Title","timezone":"Timezone","title":"Profile","subtitle":"My personal info","myInfo":"My Info","viewPublicProfile":"View Public Profile","auth":{"title":"Authentication","provider":"Provider","changePassword":"Change Password","currentPassword":"Current Password","newPassword":"New Password","verifyPassword":"Confirm New Password","changePassSuccess":"Password changed successfully."},"groups":{"title":"Groups"},"activity":{"title":"Activity","joinedOn":"Joined on","lastUpdatedOn":"Profile last updated on","lastLoginOn":"Last login on","pagesCreated":"Pages created","commentsPosted":"Comments posted"},"save":{"success":"Profile saved successfully."},"pages":{"title":"Pages","subtitle":"List of pages I created or last modified","emptyList":"No pages to display.","refreshSuccess":"Page list has been refreshed.","headerTitle":"Title","headerPath":"Path","headerCreatedAt":"Created","headerUpdatedAt":"Last Updated"},"comments":{"title":"Comments"},"preferences":"Preferences","dateFormat":"Date Format","localeDefault":"Locale Default","appearance":"Appearance","appearanceDefault":"Site Default","appearanceLight":"Light","appearanceDark":"Dark"}}	f	English	English	100	2022-04-12T06:51:27.344Z	2022-04-12T06:51:48.544Z
zh	{"common":{"footer":{"poweredBy":"Powered by","copyright":"© {{year}} {{company}}。 保留所有权利。","license":"内容由{{company}}在{{license}}下提供。"},"actions":{"save":"保存","cancel":"取消","download":"下载","upload":"上传","discard":"放弃","clear":"清除","create":"创建","edit":"编辑","delete":"删除","refresh":"刷新","saveChanges":"保存更改","proceed":"继续","ok":"OK","add":"添加","apply":"应用","browse":"浏览...","close":"关闭","page":"页面","discardChanges":"放弃更改","move":"移动","rename":"重命名","optimize":"优化","preview":"预览","properties":"属性","insert":"插入","fetch":"获取","generate":"生成","confirm":"确认","copy":"复制","returnToTop":"返回顶部","exit":"退出","select":"选择","convert":"转换"},"newpage":{"title":"此页面尚不存在。","subtitle":"你想现在创建吗？","create":"创建新页面","goback":"返回"},"unauthorized":{"title":"未经授权的","action":{"view":"您无法查看该页面。","source":"您无法查看该页面来源。","history":"您无法查看该页面历史记录。","edit":"您无法编辑该页面。","create":"您无法创建页面。","download":"你不能下载这个页面的内容。","downloadVersion":"你不能下载这个页面版本的内容。","sourceVersion":"您无法查看该页面的源码。"},"goback":"返回","login":"登录"},"notfound":{"gohome":"首页","title":"未找到","subtitle":"此页不存在。"},"welcome":{"title":"欢迎访问您的 wiki 站点！","subtitle":"让我们开始并创建首页。","createhome":"创建首页","goadmin":"管理员页面"},"header":{"home":"首页","newPage":"创建新页面","currentPage":"当前页面","view":"查看","edit":"编辑","history":"历史","viewSource":"查看源文件","move":"移动/重命名","delete":"删除","assets":"资料","imagesFiles":"图像和文件","search":"搜索...","admin":"管理","account":"账户","myWiki":"我的 Wiki","profile":"我的信息","logout":"登出","login":"登录","searchHint":"关键词不能少于 2 个字符...","searchLoading":"正在搜索...","searchNoResult":"没有符合您查询的页面。","searchResultsCount":"找到 {{total}} 条结果","searchDidYouMean":"你是指...？","searchClose":"关闭","searchCopyLink":"复制搜索链接","language":"语言","browseTags":"按标签浏览","siteMap":"站点地图","pageActions":"页面操作","duplicate":"复制","convert":"转换"},"page":{"lastEditedBy":"最后编辑","unpublished":"未发布","editPage":"编辑页面","toc":"目录","bookmark":"书签","share":"分享","printFormat":"打印格式","delete":"删除页面","deleteTitle":"确定要删除页面{{title}}吗？","deleteSubtitle":"该页面可以从管理区域恢复。","viewingSource":"查看页面 {{path}} 的源码","returnNormalView":"返回普通视图","id":"ID {{id}}","published":"已发布","private":"私密","global":"全局","loading":"正在加载页面……","viewingSourceVersion":"查看{{date}} 页面{{path}}的源码","versionId":"版本 ID {{id}}","unpublishedWarning":"此页面尚未发布。","tags":"标签","tagsMatching":"匹配标签的页面","convert":"转换页面","convertTitle":"选择您要在页面{{title}}中使用的编辑器：","convertSubtitle":"页面内容将被转换为新选择的编辑器的格式。请注意，由于转换的结果可能会丢失一些格式或未渲染的内容，一个快照将被添加到页面历史记录中，并可随时恢复。"},"error":{"unexpected":"一个意料之外的问题发生了。"},"password":{"veryWeak":"非常弱","weak":"弱","average":"普通","strong":"强","veryStrong":"非常强"},"user":{"search":"搜索用户","searchPlaceholder":"搜索用户..."},"duration":{"every":"每","minutes":"分钟","hours":"小时","days":"日","months":"月","years":"年"},"outdatedBrowserWarning":"您的浏览器已过时。升级到{{modernBrowser}} 。","modernBrowser":"新版浏览器","license":{"none":"无","ccby":"知识共享署名许可","ccbysa":"知识共享署名-相同方式共享许可","ccbynd":"知识共享署名-无衍生许可","ccbync":"知识共享署名-非商业许可","ccbyncsa":"知识共享署名-非商业性-相同方式共享许可","ccbyncnd":"知识共享署名-非商业性-无衍生许可","cc0":"公共域名","alr":"保留所有权利"},"sidebar":{"browse":"浏览","mainMenu":"主菜单","currentDirectory":"当前目录","root":"（根目录）"},"comments":{"title":"评论","newPlaceholder":"写一个新评论...","fieldName":"您的名字","fieldEmail":"您的电子邮件地址","markdownFormat":"Markdown 格式","postComment":"发表评论","loading":"评论加载中...","postingAs":"作为 {{name}} 发布","beFirst":"成为第一个发表评论的人。","none":"暂时没有评论。","updateComment":"更新评论","deleteConfirmTitle":"确认删除","deleteWarn":"您确定要永久删除此评论吗？","deletePermanentWarn":"此操作无法撤消!","modified":"{{reldate}} 已修改","postSuccess":"新评论已成功发布。","contentMissingError":"评论为空或太短！","updateSuccess":"评论已成功更新。","deleteSuccess":"评论已成功删除。","viewDiscussion":"查看讨论","newComment":"发表评论","fieldContent":"评论内容","sdTitle":"讨论"},"pageSelector":{"createTitle":"选择新页面位置","moveTitle":"移动/重命名页面位置","selectTitle":"选择一个页面","virtualFolders":"虚拟文件夹","pages":"页面","folderEmptyWarning":"此文件夹是空的。"}},"auth":{"loginRequired":"需要登录","fields":{"emailUser":"电子邮件地址 /用户名","password":"密码","email":"电子邮件地址","verifyPassword":"重复密码","name":"名字","username":"用户名"},"actions":{"login":"登录","register":"注册"},"errors":{"invalidLogin":"登录失败","invalidLoginMsg":"电子邮件地址或密码不正确","invalidUserEmail":"无效的电子邮件地址","loginError":"登录错误","notYetAuthorized":"你的账户未被授权因而无法登录。","tooManyAttempts":"尝试次数过多！","tooManyAttemptsMsg":"短时间内失败次数过多，请在 {{time}} 后再尝试。","userNotFound":"未找到用户"},"providers":{"local":"本地","windowslive":"Microsoft 账户","azure":"Azure 活动目录","google":"Google ID","facebook":"Facebook","github":"GitHub","slack":"Slack","ldap":"LDAP/活动目录"},"tfa":{"title":"双重验证","subtitle":"请输入安全代码：","placeholder":"XXXXXX","verifyToken":"验证"},"registerTitle":"创建一个账户","switchToLogin":{"text":"已经有账户？ {{link}}","link":"改为登录"},"loginUsingStrategy":"使用{{strategy}}登录","forgotPasswordLink":"忘记密码？","orLoginUsingStrategy":"或者使用以下方式登录/注册","switchToRegister":{"text":"还没有账户？ {{link}}","link":"创建一个账户"},"invalidEmailUsername":"用户名/电子邮箱无效。","invalidPassword":"输入一个有效的密码。","loginSuccess":"登录成功！正在重定向……","signingIn":"登录中...","genericError":"身份验证不可用。","registerSubTitle":"请完成下面的表单来创建账户。","pleaseWait":"请稍候","registerSuccess":"账户创建成功！","registering":"正在创建账户...","missingEmail":"请输入电子邮箱","invalidEmail":"电子邮箱无效。","missingPassword":"缺少密码。","passwordTooShort":"密码字符数过短。","passwordNotMatch":"密码不匹配。","missingName":"缺少名字。","nameTooShort":"用户名过短。","nameTooLong":"用户名太长。","forgotPasswordCancel":"取消","sendResetPassword":"重置密码","forgotPasswordSubtitle":"输入您的电子邮箱地址来接收重置密码的请求：","registerCheckEmail":"请检查您的电子邮件来激活账户。","changePwd":{"subtitle":"输入新密码","instructions":"您必须输入新密码：","newPasswordPlaceholder":"新密码","newPasswordVerifyPlaceholder":"重复新密码","proceed":"更改密码","loading":"正在更改密码……"},"forgotPasswordLoading":"正在请求密码重置...","forgotPasswordSuccess":"查看您的电子邮件以重置密码！","selectAuthProvider":"选择身份验证提供程序","enterCredentials":"输入您的凭证","forgotPasswordTitle":"忘记密码","tfaFormTitle":"输入从您信任的设备生成的安全代码：","tfaSetupTitle":"您的管理员要求您在您的账户上启用双重验证（2FA）。","tfaSetupInstrFirst":"1）使用你的双重验证程序扫描以下二维码：","tfaSetupInstrSecond":"2）输入从您信任的设备生成的安全代码："},"admin":{"dashboard":{"title":"配置板","subtitle":"Wiki.js","pages":"页面","users":"用户","groups":"组","versionLatest":"您正在运行最新版本。","versionNew":"有新版本：{{version}}","contributeSubtitle":"Wiki.js 是一个免费的开源项目。您可以通过多种方式为项目做出贡献。","contributeHelp":"我们需要你的帮助！","contributeLearnMore":"了解更多","recentPages":"最近页面","mostPopularPages":"最受欢迎的页面","lastLogins":"上次登录"},"general":{"title":"常规","subtitle":"您的 wiki 站点的主要设置","siteInfo":"网站信息","siteBranding":"网站品牌","general":"常规","siteUrl":"网站网址","siteUrlHint":"输入你的 wiki 站点的完整 URL，无需键入尾部斜杠。（例如 https://wiki.example.com）","siteTitle":"网站名称","siteTitleHint":"显示在顶部栏中, 并附加到所有页面元标题。","logo":"Logo","uploadLogo":"上传 Logo","uploadClear":"清除","uploadSizeHint":"建议使用 {{size}} 像素的图片以获得最佳效果。","uploadTypesHint":"仅限于 {{typeList}} 和 {{lastType}} 文件类型","footerCopyright":"页脚版权说明","companyName":"公司/组织名称","companyNameHint":"在页脚显示版权声明时使用的名称，留空则不显示。","siteDescription":"网站说明","siteDescriptionHint":"默认的页面描述","metaRobots":"Robots Meta 标签","metaRobotsHint":"默认：Index, Follow。也可以对每个页面单独设置。","logoUrl":"Logo URL","logoUrlHint":"选择要作为 Logo 的图片。支持 SVG、PNG、JPG，使用 34x34 以上像素的方形图片。点击右边的按钮上传新的图片。","contentLicense":"内容许可证","contentLicenseHint":"许可证在所有内容页面的页脚中显示。","siteTitleInvalidChars":"网站标题包含无效字符。","saveSuccess":"网站配置已成功保存。"},"locale":{"title":"语言环境","subtitle":"为您的 wiki 站点设置本地化选项","settings":"语言环境设置","namespacing":"多语言命名空间","downloadTitle":"下载语言环境","base":{"labelWithNS":"基本语言环境","hint":"所有 UI 文本元素都将以选定的语言显示。","label":"网站语言环境"},"autoUpdate":{"label":"自动更新","hintWithNS":"自动将更新下载到下面启用的所有命名空间语言环境。","hint":"在此语言环境可用时自动下载更新。"},"namespaces":{"label":"多语言命名空间","hint":"为同一页面启用多个语言版本。"},"activeNamespaces":{"label":"已启用的命名空间","hint":"已列出为多语言命名空间启用的语言环境。无论列表中的选择如何，始终都会包含上面定义的基本语言环境。"},"namespacingPrefixWarning":{"title":"语言环境代码将作为所有路径的前缀。 （例如/{{langCode}}/page-name）","subtitle":"如果路径中不包含语言环境代号，将自动重定向到上面定义的基本语言环境。"},"sideload":"加载区域包","sideloadHelp":"如果您没有连接到互联网或无法使用上述方法下载区域设置文件，您可以在下方上传软件包实现加载。","code":"代码","name":"名字","nativeName":"原始名称","rtl":"RTL","availability":"可用性","download":"下载"},"stats":{"title":"统计"},"theme":{"title":"主题","subtitle":"修改你 wiki 站点的外观","siteTheme":"网站主题","siteThemeHint":"主题会影响内容页的显示方式。其他网站部分 (如编辑器或管理区域) 不受影响。","darkMode":"暗模式","darkModeHint":"不建议用于辅助功能。可能不是所有主题都支持。","codeInjection":"代码注入","cssOverride":"CSS 覆盖","cssOverrideHint":"要在系统默认 CSS 之后注入的 CSS 代码。如果您有大量的 css 代码, 请考虑使用自定义主题。注入过多的 CSS 代码会导致页面加载性能不佳！CSS 将自动压缩。","headHtmlInjection":"Head 部插入 HTML","headHtmlInjectionHint":"要在结束头标记之前注入的 HTML 代码。通常用于脚本标记。","bodyHtmlInjection":"正文 HTML 注入","bodyHtmlInjectionHint":"要在结束正文标记之前注入的 HTML 代码。","downloadThemes":"下载主题","iconset":"图标集","iconsetHint":"侧边栏导航中的图标集","downloadName":"名字","downloadAuthor":"作者","downloadDownload":"下载","cssOverrideWarning":"{{caution}}为页面内容增加样式时，必须将页面内容放到 {{cssClass}} ；否则会破坏编辑器的布局。","cssOverrideWarningCaution":"注意：","options":"主题设置"},"groups":{"title":"组"},"users":{"title":"用户列表","active":"激活","inactive":"未激活","verified":"已验证","unverified":"未验证","edit":"编辑用户","id":"ID {{id}}","basicInfo":"基本信息","email":"Email","displayName":"显示名","authentication":"认证方式","authProvider":"提供者","password":"密码","changePassword":"更改密码","newPassword":"新密码","tfa":"双重验证","toggle2FA":"启用双重验证","authProviderId":"提供者 ID","groups":"用户组","noGroupAssigned":"此用户尚未分配给任何组，您必须给其分配至少一个组。","selectGroup":"选择组...","groupAssign":"分配","extendedMetadata":"扩展元数据","location":"位置","jobTitle":"工作标题","timezone":"时区","userUpdateSuccess":"用户信息更新成功。","userAlreadyAssignedToGroup":"用户已分配到此组！","deleteConfirmTitle":"删除用户账号？","deleteConfirmText":"您确定要删除用户{{username}}的账户吗？此操作无法撤销！","updateUser":"更新用户账号","groupAssignNotice":"请注意，您不能从此面板将用户分配给 Administrators 或 Guests 组。","deleteConfirmForeignNotice":"请注意，您不能删除已经创建内容的用户。相反，您必须停用用户或删除该用户创建的所有内容。","userVerifySuccess":"用户已验证。","userActivateSuccess":"用户已激活。","userDeactivateSuccess":"用户已停用。","deleteConfirmReplaceWarn":"该用户创建的任何内容（页面，上载，评论等）将重新分配给下面选择的用户。如果您不希望将内容重新分配给任何当前活动用户，建议创建一个虚拟目标用户（例如，“已删除的用户”）。","userTFADisableSuccess":"2FA 已成功禁用。","userTFAEnableSuccess":"2FA 已成功启用。"},"auth":{"title":"身份验证","subtitle":"配置您 wiki 站点的身份验证的方式","strategies":"策略","globalAdvSettings":"全局高级设置","jwtAudience":"JWT 受众","jwtAudienceHint":"登录时发出的 JWT 中的受众 URN，通常是你的域名（例如 urn:your.domain.com）","tokenExpiration":"令牌过期。","tokenExpirationHint":"令牌的最长有效期（默认：30 分钟）。","tokenRenewalPeriod":"令牌续签周期","tokenRenewalPeriodHint":"令牌续签的最长期限（默认：14 天）。","strategyState":"此策略为{{state}} {{locked}}","strategyStateActive":"激活","strategyStateInactive":"未激活","strategyStateLocked":"并且不能禁用。","strategyConfiguration":"策略配置","strategyNoConfiguration":"此策略没有可修改的配置选项。","registration":"注册","selfRegistration":"开放注册","selfRegistrationHint":"允许任何成功获得策略授权的用户访问 wiki。","domainsWhitelist":"限制到特定的电子邮件域","domainsWhitelistHint":"授权注册的域的列表。用户电子邮件地址域必须与其中之一匹配才能获得访问权限。","autoEnrollGroups":"分配给组","autoEnrollGroupsHint":"自动将新用户分配给这些组。","security":"安全性","force2fa":"强制所有用户使用双重验证  (2FA)","force2faHint":"用户将在第一次登录时被要求设置 2FA（双重验证），并且无法被用户禁用。","configReference":"配置参考","configReferenceSubtitle":"某些策略可能需要在提供程序上设置一些配置值。这些建议仅供参考，目前的战略可能不需要。","siteUrlNotSetup":"必须首先设置有效的 {{siteUrl}}}!单击左侧边栏中的 {{general}}。","allowedWebOrigins":"允许的 Web 来源","callbackUrl":"回调的网址/重定向的路径","loginUrl":"登录的网址","logoutUrl":"登出的网址","tokenEndpointAuthMethod":"令牌端点认证方法","refreshSuccess":"策略列表已更新。","saveSuccess":"验证配置已成功保存。","activeStrategies":"激活策略","addStrategy":"添加策略","strategyIsEnabled":"启用","strategyIsEnabledHint":"用户能否使用此策略登录？","displayName":"显示名称","displayNameHint":"此身份验证策略显示给用户的标题。"},"editor":{"title":"编辑器"},"logging":{"title":"日志记录"},"rendering":{"title":"渲染","subtitle":"配置页面渲染管道"},"search":{"title":"搜索引擎","subtitle":"配置该 wiki 站点的搜索功能","rebuildIndex":"重建索引","searchEngine":"搜索引擎","engineConfig":"引擎配置","engineNoConfig":"此引擎没有可修改的配置选项。","listRefreshSuccess":"搜索引擎列表已刷新。","configSaveSuccess":"搜索引擎配置已成功保存。","indexRebuildSuccess":"索引重建成功。"},"storage":{"title":"存储","subtitle":"为内容设置备份和同步目标","targets":"目标","status":"状态","lastSync":"上次同步于 {{time}}","lastSyncAttempt":"上次尝试是{{time}}","errorMsg":"错误消息","noTarget":"您没有任何活动存储目标。","targetConfig":"目标配置","noConfigOption":"此存储目标没有可修改的配置选项。","syncDirection":"同步方向","syncDirectionSubtitle":"选择如何处理此存储目标的内容同步。","syncDirBi":"双向","syncDirPush":"推送到目标","syncDirPull":"从目标中拉取","unsupported":"不支持","syncDirBiHint":"在双向模式下，首先从存储目标中提取内容。任何较新的内容都会覆盖本地内容。自上次同步以来的新内容将被推送到存储目标，如果存在，则覆盖目标上的任何内容。","syncDirPushHint":"内容始终以覆盖任何现有内容的形式被推送到存储目标，这是最安全的备份方案。","syncDirPullHint":"内容总是从存储目标中提取，覆盖已存在的任何本地内容。此选项通常保留用于一次性内容导入。警告使用此选项，因为任何本地内容都将始终被覆盖!","syncSchedule":"同步计划任务","syncScheduleHint":"出于性能原因，此存储目标按基于间隔的计划同步更改，而不是在每次更改上同步更改。定义同步发生时的间隔。","syncScheduleCurrent":"目前设置为每 {{schedule}} 。","syncScheduleDefault":"默认值为每 {{schedule}} 。","actions":"操作","actionRun":"运行","targetState":"此存储目标的状态为 {{state}}","targetStateActive":"激活","targetStateInactive":"未激活","actionsInactiveWarn":"您必须启用此存储目标并应用更改，然后才能运行操作。"},"api":{"title":"API 访问","subtitle":"管理 API 访问密钥","enabled":"API 已启用","disabled":"API 已停用","enableButton":"启用 API","disableButton":"停用 API","newKeyButton":"创建新的 API 密钥","headerName":"名称","headerKeyEnding":"密钥的最后部分（不会完全显示）","headerExpiration":"过期","headerCreated":"创建","headerLastUpdated":"最后一次更新","headerRevoke":"撤销","noKeyInfo":"尚未生成任何 API 密钥。","revokeConfirm":"确认删除此 API 密钥？","revokeConfirmText":"是否确定要撤销{{name}}密钥吗？本操作无法撤消！","revoke":"撤销","refreshSuccess":"API 密钥列表已刷新。","revokeSuccess":"已成功删除此密钥。","newKeyTitle":"新的 API 密钥","newKeySuccess":"API 密钥已成功创建。","newKeyNameError":"名称（Name）缺少或非法。","newKeyGroupError":"您必须选择一个组。","newKeyGuestGroupError":"访客组不能被用于 API 密钥。","newKeyNameHint":"此密钥的用途","newKeyName":"名称","newKeyExpiration":"过期","newKeyExpirationHint":"无论密钥是否到期，你都可以随时撤销。","newKeyPermissionScopes":"权限范围","newKeyFullAccess":"完全访问","newKeyGroupPermissions":"或使用用户组权限……","newKeyGroup":"用户组","newKeyGroupHint":"API 密钥将具有与所选组相同的权限。","expiration30d":"30 天","expiration90d":"90 天","expiration180d":"180 天","expiration1y":"1 年","expiration3y":"3 年","newKeyCopyWarn":"复制如下所示的密钥{{bold}}","newKeyCopyWarnBold":"它将不会再显示","toggleStateEnabledSuccess":"API 已成功启用。","toggleStateDisabledSuccess":"API 已成功禁用。"},"system":{"title":"系统信息","subtitle":"有关您系统的信息","hostInfo":"主机信息","currentVersion":"当前版本","latestVersion":"最新版本","published":"发布于","os":"操作系统","hostname":"主机名","cpuCores":"CPU 核心数","totalRAM":"总内存","workingDirectory":"工作目录","configFile":"配置文件","ramUsage":"RAM 使用情况：{{used}} / {{total}}","dbPartialSupport":"您的数据库版本不受完全支持。部分功能可能受限，或不会按预期正常工作。","refreshSuccess":"已刷新系统信息。"},"utilities":{"title":"其他","subtitle":"维护和杂项工具","tools":"工具","authTitle":"认证","authSubtitle":"用于身份验证/用户的各种工具","cacheTitle":"刷新缓存","cacheSubtitle":"各种组件的刷新缓存","graphEndpointTitle":"GraphQL 端点","graphEndpointSubtitle":"更改 Wiki.js 的 GraphQL 端点","importv1Title":"从 1.x 的 Wiki.js 导入","importv1Subtitle":"从 1.x 版本中迁移数据","telemetryTitle":"遥测","telemetrySubtitle":"启用/禁用遥测或重置客户端 ID","contentTitle":"内容","contentSubtitle":"页面工具"},"dev":{"title":"开发者工具","flags":{"title":"标志"},"graphiql":{"title":"GraphiQL"},"voyager":{"title":"航海家"}},"contribute":{"title":"为 Wiki.js 出一份力","subtitle":"帮助支持 Wiki.js 开发和运营","fundOurWork":"资助我们的工作","spreadTheWord":"把信息传播出去","talkToFriends":"让您的朋友和同事知道 Wiki.js 是多么的棒!","followUsOnTwitter":"在 {{0}} 关注我们。","submitAnIdea":"在 {{0}} 上提交想法或对提议的想法进行投票。","submitAnIdeaLink":"功能请求板","foundABug":"发现了一个 bug？在 {{0}} 上提交问题。","helpTranslate":"帮助翻译 Wiki.js 到您所使用的语言。请在 {{0}} 告诉我们。","makeADonation":"进行捐赠","contribute":"贡献","openCollective":"Wiki.js 也是 Open Collective 计划——一个面向开源社区的公开基金的一部分。您可以通过每月或一次性捐款来提供财务捐助：","needYourHelp":"我们需要您的帮助来不断改进软件并运行各种相关服务（例如托管和网络）。","openSource":"Wiki.js 是 {{1}} 和 {{2}} 用 {{0}} 为您提供的免费开源软件。","openSourceContributors":"贡献者","tshirts":"您还可以购买 Wiki.js T恤以在财务上支持该项目：","shop":"Wiki.js 商店","becomeAPatron":"进行赞助","patreon":"通过 Patreon 成为赞助者（直接支持首席开发者 Nicolas Giard 以帮助他实现全职开发 Wiki.js 的目标）","paypal":"通过 Paypal 进行一次性或定期捐赠：","ethereum":"我们接受使用以太坊的赞助：","github":"通过 GitHub Sponsors 成为赞助者（直接支持首席开发者 Nicolas Giard 以帮助他实现全职开发 Wiki.js 的目标）","becomeASponsor":"进行赞助"},"nav":{"site":"网站","users":"用户","modules":"模块","system":"系统"},"pages":{"title":"页面"},"navigation":{"title":"导航","subtitle":"管理网站导航","link":"链接","divider":"分隔器","header":"头部区域","label":"标签","icon":"图标","targetType":"目标类型","target":"目标","noSelectionText":"在左侧选择一个导航项。","untitled":"无标题 {{kind}}","navType":{"external":"外部链接","home":"首页","page":"页面","searchQuery":"搜索查询","externalblank":"外部链接（新窗口）"},"edit":"编辑{{kind}}","delete":"删除{{kind}}","saveSuccess":"导航已成功保存。","noItemsText":"单击添加按钮添加第一个导航项。","emptyList":"空导航","visibilityMode":{"all":"所有人可见","restricted":"选定组以对该组可见……"},"selectPageButton":"选择页面...","mode":"导航模式","modeSiteTree":{"title":"站点地图","description":"经典树状导航"},"modeCustom":{"title":"自定义导航","description":"静态导航菜单 + 站点树按钮"},"modeNone":{"title":"无","description":"禁用站点导航栏"},"copyFromLocale":"从语言环境复制...","sourceLocale":"源语言","sourceLocaleHint":"将要被复制的导航栏物件的语言。","copyFromLocaleInfoText":"从要复制的项目中选择语言。项目将被添加到当前激活的语言列表之后。","modeStatic":{"title":"静态导航","description":"仅静态导航菜单"}},"mail":{"title":"邮件","subtitle":"配置邮件设置","configuration":"配置","dkim":"DKIM（可选）","test":"发送测试邮件","testRecipient":"收件人的 Email","testSend":"发送测试邮件","sender":"发件人","senderName":"发件人姓名","senderEmail":"发件人的 Email","smtp":"SMTP 设置","smtpHost":"主机","smtpPort":"端口","smtpPortHint":"通常为 465（推荐），587 或 25。","smtpTLS":"安全（TLS）","smtpTLSHint":"使用端口 465 时应启用，否则关闭（例如使用端口 587 或 25）。","smtpUser":"用户名","smtpPwd":"密码","dkimHint":"DKIM（DomainKeys Identified Mail）通过为收件人提供验证域名和确保邮件真实性的方法，为从 Wiki.js 发送的所有电子邮件提供一层安全性。","dkimUse":"使用 DKIM","dkimDomainName":"域名","dkimKeySelector":"密钥选择器","dkimPrivateKey":"私钥","dkimPrivateKeyHint":"PEM 格式的选择器的私钥","testHint":"发送测试邮件，以确保您的 SMTP 配置已经正常工作。","saveSuccess":"已保存设置。","sendTestSuccess":"已成功发送测试邮件。","smtpVerifySSL":"验证 SSL 证书","smtpVerifySSLHint":"某些主机要求禁用 SSL 证书检查来继续，但我们推荐保持启用来确保安全性。"},"webhooks":{"title":"Webhook","subtitle":"管理外部服务的 Webhook"},"adminArea":"管理区","analytics":{"title":"分析","subtitle":"将分析和跟踪工具添加到您的 wiki 站点","providers":"供应商","providerConfiguration":"配置选项","providerNoConfiguration":"该供应商没有可修改的配置选项。","refreshSuccess":"已刷新供应商名单。","saveSuccess":"已成功保存分析配置"},"comments":{"title":"评论","provider":"提供者","subtitle":"向您的 wiki 站点的页面添加讨论功能","providerConfig":"提供者配置","providerNoConfig":"该提供者没有可供您修改的配置选项。"},"tags":{"title":"标签","subtitle":"管理页面标签","emptyList":"没有标签可显示。","edit":"编辑标签","tag":"标签","label":"标记","date":"创建于 {{created}}，最后更新于 {{updated}}。","delete":"删除这个标签","noSelectionText":"从左侧列表中选择一个标签","noItemsText":"为页面增加一个新标签吧。","refreshSuccess":"标签已刷新。","deleteSuccess":"标签删除成功。","saveSuccess":"标签已成功保存。","filter":"筛选...","viewLinkedPages":"查看被链接到的页面","deleteConfirm":"删除标签？","deleteConfirmText":"您确定要删除标签{{tag}}吗？该标签还将与所有页面取消链接。"},"ssl":{"title":"SSL 证书","subtitle":"管理 SSL 配置","provider":"供应商","providerHint":"如果你已经拥有自己的证书，请选择“自定义证书”。","domain":"域名","domainHint":"输入指向你的 wiki 站点的完整域名（例如：wiki.example.com）","providerOptions":"供应商选项","providerDisabled":"禁用","providerLetsEncrypt":"Let's Encrypt","providerCustomCertificate":"自定义证书","ports":"端口","httpPort":"HTTP 端口","httpPortHint":"非 SSL 服务器端口将会监听 HTTP 请求，这些端口一般是 80 或 3000 。","httpsPort":"HTTPS 端口","httpsPortHint":"SSL 服务器监听的 HTTPS 请求端口，一般是 443。","httpPortRedirect":"HTTP 请求重定向到 HTTPS","httpPortRedirectHint":"将会自动将所有的 HTTP 请求重定向到 HTTPS。","writableConfigFileWarning":"请注意，你的配置文件必须为可写入才能设定端口配置。","renewCertificate":"续期证书","status":"证书状态","expiration":"证书过期","subscriberEmail":"订阅者邮箱","currentState":"当前状态","httpPortRedirectTurnOn":"打开","httpPortRedirectTurnOff":"关闭","renewCertificateLoadingTitle":"正在续期证书……","renewCertificateLoadingSubtitle":"请不要离开此页面。","renewCertificateSuccess":"证书续期成功。","httpPortRedirectSaveSuccess":"HTTP 重定向已成功更改。"},"security":{"title":"安全","maxUploadSize":"最大上传大小","maxUploadBatch":"每次上传的最大文件数","maxUploadBatchHint":"一次可以批量上传多少文件？","maxUploadSizeHint":"单个文件的最大大小。","maxUploadSizeSuffix":"字节","maxUploadBatchSuffix":"文件","uploads":"上传","uploadsInfo":"这些设置只影响 Wiki.js。 如果您使用反向代理(例如 Nginx、Apache、Cloudflare)，您必须更改它们的设置以生效","subtitle":"配置安全设置","login":"登录","loginScreen":"登录页","jwt":"JWT 配置","bypassLogin":"绕过登录页面","bypassLoginHint":"是否自动将用户重定向到第一个身份验证提供程序。","loginBgUrl":"登录页面背景图片链接","loginBgUrlHint":"指定要用作登录背景的图像。支持 PNG 和 JPG，建议使用图片尺寸为 1920x1080。默认为空。点击右侧的按钮上传新图像。请注意，Guests 组必须对所选图像具有读取权限！","hideLocalLogin":"隐藏本地身份验证提供程序","hideLocalLoginHint":"不要在登录屏幕上显示本地身份验证提供程序。 在 URL 后加上 ?all 可以暂时使用它。","loginSecurity":"安全","enforce2fa":"强制双重验证","enforce2faHint":"在使用带有 用户名 / 密码 形式的身份验证提供程序时，强制所有用户使用双重验证。"},"extensions":{"title":"扩展","subtitle":"安装扩展以获得额外功能"}},"editor":{"page":"页面设置","save":{"processing":"渲染中","pleaseWait":"请稍候","createSuccess":"页面创建成功。","error":"创建页面时发生错误","updateSuccess":"页面已成功更新。","saved":"已保存"},"props":{"pageProperties":"页面属性","pageInfo":"页面信息","title":"标题","shortDescription":"简短说明","shortDescriptionHint":"标题下方显示","pathCategorization":"路径和分类","locale":"语言环境","path":"路径","pathHint":"不要包含任何前导或尾随斜杠。","tags":"标签","tagsHint":"使用标记对页面进行分类,使其更易于查找。","publishState":"发布状态","publishToggle":"已发布","publishToggleHint":"在此页上具有写入权限的用户仍可以看到未发布的页面。","publishStart":"发布结束...","publishStartHint":"留空表示没有起始日期。","publishEnd":"发布结束...","publishEndHint":"留空表示没有结束日期","info":"页面信息","scheduling":"定时任务","social":"社交","categorization":"分类","socialFeatures":"社交功能","allowComments":"允许评论","allowCommentsHint":"在此页面上启用评论功能。","allowRatings":"允许评分","displayAuthor":"显示作者信息","displaySharingBar":"显示共享工具栏","displaySharingBarHint":"显示工具栏来共享和打印此页面","displayAuthorHint":"显示页面作者及上次编辑时间。","allowRatingsHint":"在此页面上启用评分功能。","scripts":"脚本","css":"CSS","cssHint":"CSS 将会在保存后自动压缩 。不要添加 style 标签，只需要 CSS 代码。","styles":"样式","html":"HTML","htmlHint":"您必须用 HTML script 标签将 JavaScript 代码包裹起来。"},"unsaved":{"title":"放弃未保存的更改？","body":"您有未保存的更改。是否确实要离开编辑器并放弃自上次保存以来所做的任何修改？"},"select":{"title":"你想使用哪个编辑器？","cannotChange":"创建页面后无法更改此项。","customView":"还是创建自定义视图？"},"assets":{"title":"资源","newFolder":"新建文件夹","folderName":"文件夹名称","folderNameNamingRules":"必须遵循 {{namingRules}}","folderNameNamingRulesLink":"命名规则","folderEmpty":"空文件夹","fileCount":"{{count}} 个文件","headerId":"ID","headerFilename":"文件名","headerType":"类型","headerFileSize":"文件大小","headerAdded":"已添加","headerActions":"操作","uploadAssets":"上传","uploadAssetsDropZone":"浏览或拖放文件到此区域...","fetchImage":"获取远程图像","imageAlign":"图像对齐","renameAsset":"重命名","renameAssetSubtitle":"输入新名称：","deleteAsset":"删除","deleteAssetConfirm":"确定删除？","deleteAssetWarn":"此操作无法撤消！","refreshSuccess":"已刷新的文件列表。","uploadFailed":"文件上传失败。","folderCreateSuccess":"已成功创建文件夹。","renameSuccess":"已成功重命名。","deleteSuccess":"已成功删除。","noUploadError":"你必须选择一个要上传的文件！"},"backToEditor":"返回编辑","markup":{"bold":"加粗","italic":"斜体","strikethrough":"删除线","heading":"标题{{level}}","subscript":"下标","superscript":"上标","blockquote":"块引用","blockquoteInfo":"信息型块引用","blockquoteSuccess":"成功型块引用","blockquoteWarning":"警告型块引用","blockquoteError":"错误型块引用","unorderedList":"未排序列表","orderedList":"有序列表","inlineCode":"插入代码块","keyboardKey":"键盘文本","horizontalBar":"分隔符","togglePreviewPane":"显示/隐藏预览窗格","insertLink":"插入链接","insertAssets":"插入文件","insertBlock":"插入块","insertCodeBlock":"插入代码块","insertVideoAudio":"插入视频/音频","insertDiagram":"插入绘图","insertMathExpression":"插入数学表达式","tableHelper":"表格助手","distractionFreeMode":"勿扰模式","markdownFormattingHelp":"Markdown 用法帮助","noSelectionError":"必须先选择文本！","toggleSpellcheck":"切换拼写检查"},"ckeditor":{"stats":"{{chars}}字符， {{words}}单词"},"conflict":{"title":"解决保存冲突","useLocal":"使用本地","useRemote":"使用远程","useRemoteHint":"放弃本地更改并使用最新版本","useLocalHint":"使用左侧面板中的内容","viewLatestVersion":"查看最新版本","infoGeneric":"该页面的最新版本由 {{authorName}} 在 {{date}} 保存","whatToDo":"您将如何选择？","whatToDoLocal":"使用当前的本地版本，并忽略最新的更改。","whatToDoRemote":"使用远程版本(最新) 并放弃您的更改。","overwrite":{"title":"用远程版本覆盖？","description":"您确定要将当前版本替换为最新的远程版本内容吗？{{refEditsLost}}","editsLost":"您当前的修改将丢失。"},"localVersion":"本地版本 {{refEditable}}","editable":"（可编辑）","readonly":"（只读）","remoteVersion":"远程版本 {{refReadOnly}}","leftPanelInfo":"您当前的修改基于 {{date}} 页面版本","rightPanelInfo":"最后由 {{authorName}} 于 {{date}} 编辑","pageTitle":"标题：","pageDescription":"说明：","warning":"保存冲突！另一个用户已经修改了此页面。"},"unsavedWarning":"您有未保存的修改。 是否确认离开编辑器？"},"tags":{"currentSelection":"当前选择","clearSelection":"清空选择","selectOneMoreTags":"选择一个或多个标签","searchWithinResultsPlaceholder":"在结果中搜索...","locale":"区域设置","orderBy":"排序","selectOneMoreTagsHint":"在左侧选择一个或多个标签。","retrievingResultsLoading":"正在检索页面...","noResults":"找不到与选中标签匹配的页面。","noResultsWithFilter":"找不到与当前筛选项匹配的页面。","pageLastUpdated":"最后更新 {{date}}","orderByField":{"creationDate":"创建时间","ID":"ID","lastModified":"最后编辑","path":"路径","title":"标题"},"localeAny":"任何"},"history":{"restore":{"confirmTitle":"恢复页面版本？","confirmText":"您确定要恢复页面内容到 {{date}}的版本吗? 该版本将会被复制到当前历史的最新版本。因此，较新版本仍然会被保留在历史记录中。","confirmButton":"恢复","success":"页面版本成功恢复！"}},"profile":{"displayName":"显示名称","location":"地区","jobTitle":"职位","timezone":"时区","title":"配置文件","subtitle":"我的个人信息","myInfo":"我的信息","viewPublicProfile":"查看公开资料","auth":{"title":"身份验证","provider":"提供者","changePassword":"修改密码","currentPassword":"当前密码","newPassword":"新密码","verifyPassword":"确认新密码","changePassSuccess":"密码修改成功。"},"groups":{"title":"用户组"},"activity":{"title":"活动","joinedOn":"加入于","lastUpdatedOn":"资料最后更新于","lastLoginOn":"上次登录于","pagesCreated":"页面已创建","commentsPosted":"评论已发布"},"save":{"success":"资料保存成功。"},"pages":{"title":"页面","subtitle":"我创建或最后修改的页面列表","emptyList":"没有可以显示的页面。","refreshSuccess":"页面列表已刷新。","headerTitle":"标题","headerPath":"路径","headerCreatedAt":"创建","headerUpdatedAt":"最后一次更新"},"comments":{"title":"评论"},"preferences":"偏好设置","dateFormat":"日期格式","localeDefault":"默认语言","appearance":"外观","appearanceDefault":"站点默认外观","appearanceLight":"亮模式","appearanceDark":"暗模式"}}	f	Chinese Simplified	中文 (Zhōngwén)	100	2022-04-12T06:52:15.101Z	2022-04-12T06:52:15.101Z
\.


--
-- Data for Name: loggers; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.loggers (key, "isEnabled", level, config) FROM stdin;
airbrake	f	warn	{}
bugsnag	f	warn	{"key":""}
disk	f	info	{}
eventlog	f	warn	{}
loggly	f	warn	{"token":"","subdomain":""}
logstash	f	warn	{}
newrelic	f	warn	{}
papertrail	f	warn	{"host":"","port":0}
raygun	f	warn	{}
rollbar	f	warn	{"key":""}
sentry	f	warn	{"key":""}
syslog	f	warn	{}
\.


--
-- Data for Name: migrations; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.migrations (id, name, batch, migration_time) FROM stdin;
1	2.0.0.js	1	2022-04-12 06:49:36.797+00
2	2.1.85.js	1	2022-04-12 06:49:36.8+00
3	2.2.3.js	1	2022-04-12 06:49:36.826+00
4	2.2.17.js	1	2022-04-12 06:49:36.831+00
5	2.3.10.js	1	2022-04-12 06:49:36.833+00
6	2.3.23.js	1	2022-04-12 06:49:36.835+00
7	2.4.13.js	1	2022-04-12 06:49:36.839+00
8	2.4.14.js	1	2022-04-12 06:49:36.857+00
9	2.4.36.js	1	2022-04-12 06:49:36.864+00
10	2.4.61.js	1	2022-04-12 06:49:36.867+00
11	2.5.1.js	1	2022-04-12 06:49:36.875+00
12	2.5.12.js	1	2022-04-12 06:49:36.877+00
13	2.5.108.js	1	2022-04-12 06:49:36.878+00
14	2.5.118.js	1	2022-04-12 06:49:36.88+00
15	2.5.122.js	1	2022-04-12 06:49:36.9+00
16	2.5.128.js	1	2022-04-12 06:49:36.904+00
\.


--
-- Data for Name: migrations_lock; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.migrations_lock (index, is_locked) FROM stdin;
1	0
\.


--
-- Data for Name: navigation; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.navigation (key, config) FROM stdin;
site	[{"locale":"en","items":[{"id":"79278832-f5dc-4556-8bf7-453db010f428","icon":"mdi-home","kind":"link","label":"Home","target":"/","targetType":"home","visibilityMode":"all","visibilityGroups":null}]}]
\.


--
-- Data for Name: pageHistory; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."pageHistory" (id, path, hash, title, description, "isPrivate", "isPublished", "publishStartDate", "publishEndDate", action, "pageId", content, "contentType", "createdAt", "editorKey", "localeCode", "authorId", "versionDate", extra) FROM stdin;
\.


--
-- Data for Name: pageHistoryTags; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."pageHistoryTags" (id, "pageId", "tagId") FROM stdin;
\.


--
-- Data for Name: pageLinks; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."pageLinks" (id, path, "localeCode", "pageId") FROM stdin;
\.


--
-- Data for Name: pageTags; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."pageTags" (id, "pageId", "tagId") FROM stdin;
\.


--
-- Data for Name: pageTree; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."pageTree" (id, path, depth, title, "isPrivate", "isFolder", "privateNS", parent, "pageId", "localeCode", ancestors) FROM stdin;
\.


--
-- Data for Name: pages; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.pages (id, path, hash, title, description, "isPrivate", "isPublished", "privateNS", "publishStartDate", "publishEndDate", content, render, toc, "contentType", "createdAt", "updatedAt", "editorKey", "localeCode", "authorId", "creatorId", extra) FROM stdin;
\.


--
-- Data for Name: pagesVector; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."pagesVector" (id, path, locale, title, description, tokens, content) FROM stdin;
\.


--
-- Data for Name: pagesWords; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."pagesWords" (word) FROM stdin;
\.


--
-- Data for Name: renderers; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.renderers (key, "isEnabled", config) FROM stdin;
htmlAsciinema	f	{}
htmlBlockquotes	t	{}
htmlCodehighlighter	t	{}
htmlCore	t	{"absoluteLinks":false,"openExternalLinkNewTab":false,"relAttributeExternalLink":"noreferrer"}
htmlDiagram	t	{}
htmlImagePrefetch	f	{}
htmlMediaplayers	t	{}
htmlMermaid	t	{}
htmlSecurity	t	{"safeHTML":true,"allowDrawIoUnsafe":true,"allowIFrames":false}
htmlTabset	t	{}
htmlTwemoji	t	{}
markdownAbbr	t	{}
markdownCore	t	{"allowHTML":true,"linkify":true,"linebreaks":true,"underline":false,"typographer":false,"quotes":"English"}
markdownEmoji	t	{}
markdownExpandtabs	t	{"tabWidth":4}
markdownFootnotes	t	{}
markdownImsize	t	{}
markdownKatex	t	{"useInline":true,"useBlocks":true}
markdownKroki	f	{"server":"https://kroki.io","openMarker":"```kroki","closeMarker":"```"}
markdownMathjax	f	{"useInline":true,"useBlocks":true}
markdownMultiTable	f	{"multilineEnabled":true,"headerlessEnabled":true,"rowspanEnabled":true}
markdownPlantuml	t	{"server":"https://plantuml.requarks.io","openMarker":"```plantuml","closeMarker":"```","imageFormat":"svg"}
markdownSupsub	t	{"subEnabled":true,"supEnabled":true}
markdownTasklists	t	{}
openapiCore	t	{}
\.


--
-- Data for Name: searchEngines; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."searchEngines" (key, "isEnabled", config) FROM stdin;
aws	f	{"domain":"","endpoint":"","region":"us-east-1","accessKeyId":"","secretAccessKey":"","AnalysisSchemeLang":"en"}
algolia	f	{"appId":"","apiKey":"","indexName":"wiki"}
azure	f	{"serviceName":"","adminKey":"","indexName":"wiki"}
db	f	{}
postgres	t	{"dictLanguage":"simple"}
elasticsearch	f	{"apiVersion":"6.x","hosts":"","indexName":"wiki","analyzer":"simple","sniffOnStart":false,"sniffInterval":0}
manticore	f	{}
solr	f	{"host":"solr","port":8983,"core":"wiki","protocol":"http"}
sphinx	f	{}
\.


--
-- Data for Name: sessions; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.sessions (sid, sess, expired) FROM stdin;
\.


--
-- Data for Name: settings; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.settings (key, value, "updatedAt") FROM stdin;
auth	{"audience":"urn:wiki.js","tokenExpiration":"30m","tokenRenewal":"14d"}	2022-04-12T06:51:26.219Z
certs	{"jwk":{"kty":"RSA","n":"qX2njyTISTao6TXq1wPv7nmo5Gdq0-1jlGtxmoMoiRN52ppUszhEvVu8emeeQtDW6InnACO1YgAY1y_eaFAnSIYDDuGHeGZzlHiKKu5KOfnyxhxoyiKsSODJfWH4ElG_Q545twj8io2wvpxgnGzrQi0uuese3ULTpWXAlxZGFlcZhBJoLpkpXJLIsdkXOtnc5N9hfDVG1XtZu8rhNwwuRuN1c7JIojpes0EFjntEVI266spMdrfpfWET4frO2ttIWqCHgHjahymdLfUEHvLTYtqVlSjFbNA0IJ2JoXQGVWKK2wL4MTKIw9XBeJmoIEYIt_5Q487M7aJ2G5Yjp9kPQw","e":"AQAB"},"public":"-----BEGIN RSA PUBLIC KEY-----\\nMIIBCgKCAQEAqX2njyTISTao6TXq1wPv7nmo5Gdq0+1jlGtxmoMoiRN52ppUszhE\\nvVu8emeeQtDW6InnACO1YgAY1y/eaFAnSIYDDuGHeGZzlHiKKu5KOfnyxhxoyiKs\\nSODJfWH4ElG/Q545twj8io2wvpxgnGzrQi0uuese3ULTpWXAlxZGFlcZhBJoLpkp\\nXJLIsdkXOtnc5N9hfDVG1XtZu8rhNwwuRuN1c7JIojpes0EFjntEVI266spMdrfp\\nfWET4frO2ttIWqCHgHjahymdLfUEHvLTYtqVlSjFbNA0IJ2JoXQGVWKK2wL4MTKI\\nw9XBeJmoIEYIt/5Q487M7aJ2G5Yjp9kPQwIDAQAB\\n-----END RSA PUBLIC KEY-----\\n","private":"-----BEGIN RSA PRIVATE KEY-----\\nProc-Type: 4,ENCRYPTED\\nDEK-Info: AES-256-CBC,5887EEA86889505F6E3E6DAF4D1240A0\\n\\nU5Dr1WSZZMLnQtRBnVAO0z5Zju5uoqrc0LfJpUWd5jkCTXv+5GEwdrQWYvBDKznN\\nagXDBqnsR5NnNN7bnIN/6o3G3FAmf0g4R3fOYhPFWKYfl8qg7vNc9GAAINOtLlyl\\n44SkKOYaQ6nPlrGqrMsdRgyf/9ejS1rFON9Ouk5i1gv3LS/tx/Vc69JoFjitEEWw\\nI4UYp9Axp/Wy0UT1zzdiTzGGcDJpaAtXrkuvnfcMpEVbFukVe9J6lo/+QN0smryh\\ne97HZmXjJlCh+L+It0dEIvf6drEaBoVJA0sbcJzoSGkl1fBSvc4A0gwcReosToXb\\n3bgNcBO4zPqmhq04wWEtPXqAQUKdoez5LdOekkswYV/xuw7v7PEFXQh0QBr/7vO6\\nNM2a8r+XA/C0EQu3AGdlLFbw7IP/he4OXbshBw9lHaKnO5VbaxQohjNOcgfyjxD0\\njreYsuOaw5gn7hNX08kcaEC/WhcETwo+J6WzVIx4tbjWBKnRSclIOlflysKj083p\\nJb4BV58xMk0EHKPJaMwsnB62gseyw+sCbL0GAHLqcaq9Pzjz3xYvmXgfx90VdFXr\\nnJd11OkktiVpa7+2JWwU4/X2uvjRkgB6icWLZgMYiMKb556BN0RoLJ5WJWZtBT1C\\n0ow+YjGeVtcjgjWaR1l/iwZ3jtunLu93jNsSPdRKr6aGqcpNJ7oJOFMWd05aVRDY\\nwJaDYQ30+FhHL3VSN0ba8bHG4QfWaMC3NNTMIVvXvguLHr9uLxGViitNtj75Rn0D\\nm3LTGhgq6pxU94bRlc+1BoFV6CqEus++Sg3g5jAeaN48dF1YdN5+2PLgOuTy6kTb\\nzxICZkulxTbWolmb9H8cgRb5k1T5j2n01u12tr43SLbQBizl90BBOma+ogyql2jJ\\nD21W8nHx9kYJhAhmK/kAxQwRmAF3J3dff/Z81DBKIA+RFRVcHeDteRGUePkF2DMD\\nbkktQWTq0TKQQxOXGvOYAHn9U4vr+RzbSTRFlqjCIHusQlfnYvewyWL8Wx8cpPFo\\nDVIEVNXvnV3MjewQ3tAXRicPy/RaOgAZ5pGp4m2ZGWtDaarzucYzdnEGh0EFBRZD\\nmtqNPTGzTaNxiAzuWnoI1OSerclg4NELHUurTl8KusfhbSbPEPP8dUECdbPcry7q\\nboDyFPiwa1eAvurJu5Q0btG3k/of75G/4xGlpq1P2BwuRPFGTaephGPmowiTOXIA\\nvqWo6yGDGEhbADcBIaFg9vsfGPiZk4y//BvP+3uB7adHhlIIaR/E2xJMJ7HgroWL\\n0ymIsdDGvd7kj2enNLAAITVFaITk1aVTc3FLe4N2JdkABwrI9mHEkW8XDgVsjYld\\nn9ttp1yVdrrRZSgfQLqIYr+rtKnavb+X3y3IfR5GCOYtkeHOaOIq8AJd5ywk6k7v\\nol+6TRP+bkhxMdsI5eqJDQcGpeML35aDhMakFGXVDdXQ/On6xoslFsFi43TGNILF\\nRpD62lMDWxVXudrOh6/JUtnChX7ETUlF2rujSSrP/1BDKOuFiOlwSWFJaLMFPlQ9\\nYQUEOSKaOtueaJ2vJSm3nZN9gOy/EyIHprwlLhuXF46tVq5nwBxFPRSYSAyS2BDd\\n-----END RSA PRIVATE KEY-----\\n"}	2022-04-12T06:51:26.237Z
company	{"v":""}	2022-04-12T06:51:26.258Z
features	{"featurePageRatings":true,"featurePageComments":true,"featurePersonalWikis":true}	2022-04-12T06:51:26.270Z
graphEndpoint	{"v":"https://graph.requarks.io"}	2022-04-12T06:51:26.274Z
host	{"v":"http://wiki.fjjfypt.com"}	2022-04-12T06:51:26.279Z
logo	{"hasLogo":false,"logoIsSquare":false}	2022-04-12T06:51:26.300Z
mail	{"senderName":"","senderEmail":"","host":"","port":465,"secure":true,"verifySSL":true,"user":"","pass":"","useDKIM":false,"dkimDomainName":"","dkimKeySelector":"","dkimPrivateKey":""}	2022-04-12T06:51:26.308Z
seo	{"description":"","robots":["index","follow"],"analyticsService":"","analyticsId":""}	2022-04-12T06:51:26.318Z
sessionSecret	{"v":"422c2a48570c87ec9b1d1e5ed07aba5b71f6fbed62e577146134ecfb8daae39a"}	2022-04-12T06:51:26.327Z
telemetry	{"isEnabled":true,"clientId":"d06aa9bc-f5f8-481f-a690-fd1f6bf0ccac"}	2022-04-12T06:51:26.336Z
theming	{"theme":"default","darkMode":false,"iconset":"mdi","injectCSS":"","injectHead":"","injectBody":""}	2022-04-12T06:51:26.340Z
uploads	{"maxFileSize":5242880,"maxFiles":10,"scanSVG":true,"forceDownload":true}	2022-04-12T06:51:26.345Z
title	{"v":"Wiki.js"}	2022-04-12T06:51:26.348Z
lang	{"code":"zh","autoUpdate":true,"namespacing":false,"namespaces":["zh"],"rtl":false}	2022-04-12T06:52:20.846Z
\.


--
-- Data for Name: storage; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.storage (key, "isEnabled", mode, config, "syncInterval", state) FROM stdin;
azure	f	push	{"accountName":"","accountKey":"","containerName":"wiki","storageTier":"Cool"}	P0D	{"status":"pending","message":"","lastAttempt":null}
box	f	push	{"clientId":"","clientSecret":"","rootFolder":""}	P0D	{"status":"pending","message":"","lastAttempt":null}
digitalocean	f	push	{"endpoint":"nyc3.digitaloceanspaces.com","bucket":"","accessKeyId":"","secretAccessKey":""}	P0D	{"status":"pending","message":"","lastAttempt":null}
disk	f	push	{"path":"","createDailyBackups":false}	P0D	{"status":"pending","message":"","lastAttempt":null}
dropbox	f	push	{"appKey":"","appSecret":""}	P0D	{"status":"pending","message":"","lastAttempt":null}
gdrive	f	push	{"clientId":"","clientSecret":""}	P0D	{"status":"pending","message":"","lastAttempt":null}
git	f	sync	{"authType":"ssh","repoUrl":"","branch":"master","sshPrivateKeyMode":"path","sshPrivateKeyPath":"","sshPrivateKeyContent":"","verifySSL":true,"basicUsername":"","basicPassword":"","defaultEmail":"name@company.com","defaultName":"John Smith","localRepoPath":"./data/repo","gitBinaryPath":""}	PT5M	{"status":"pending","message":"","lastAttempt":null}
onedrive	f	push	{"clientId":"","clientSecret":""}	P0D	{"status":"pending","message":"","lastAttempt":null}
s3	f	push	{"region":"","bucket":"","accessKeyId":"","secretAccessKey":""}	P0D	{"status":"pending","message":"","lastAttempt":null}
s3generic	f	push	{"endpoint":"https://service.region.example.com","bucket":"","accessKeyId":"","secretAccessKey":"","sslEnabled":true,"s3ForcePathStyle":false,"s3BucketEndpoint":false}	P0D	{"status":"pending","message":"","lastAttempt":null}
sftp	f	push	{"host":"","port":22,"authMode":"privateKey","username":"","privateKey":"","passphrase":"","password":"","basePath":"/root/wiki"}	P0D	{"status":"pending","message":"","lastAttempt":null}
\.


--
-- Data for Name: tags; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.tags (id, tag, title, "createdAt", "updatedAt") FROM stdin;
\.


--
-- Data for Name: userAvatars; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."userAvatars" (id, data) FROM stdin;
\.


--
-- Data for Name: userGroups; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."userGroups" (id, "userId", "groupId") FROM stdin;
1	1	1
2	2	2
\.


--
-- Data for Name: userKeys; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public."userKeys" (id, kind, token, "createdAt", "validUntil", "userId") FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: wikijs
--

COPY public.users (id, email, name, "providerId", password, "tfaIsActive", "tfaSecret", "jobTitle", location, "pictureUrl", timezone, "isSystem", "isActive", "isVerified", "mustChangePwd", "createdAt", "updatedAt", "providerKey", "localeCode", "defaultEditor", "lastLoginAt", "dateFormat", appearance) FROM stdin;
2	guest@example.com	Guest	\N		f	\N			\N	America/New_York	t	t	t	f	2022-04-12T06:51:28.729Z	2022-04-12T06:51:28.729Z	local	en	markdown	\N		
1	admin@jfy.com	Administrator	\N	$2a$12$ilqNst4.lfOmDfRXzISXEOTkVCE2DzUPJTkAEy.Fk5nhjYl1cqrEC	f	\N			\N	America/New_York	f	t	t	f	2022-04-12T06:51:28.114Z	2022-04-12T06:51:28.114Z	local	en	markdown	2022-04-12T06:51:59.071Z		
\.


--
-- Name: apiKeys_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."apiKeys_id_seq"', 1, false);


--
-- Name: assetFolders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."assetFolders_id_seq"', 1, false);


--
-- Name: assets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public.assets_id_seq', 1, false);


--
-- Name: comments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public.comments_id_seq', 1, false);


--
-- Name: groups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public.groups_id_seq', 2, true);


--
-- Name: migrations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public.migrations_id_seq', 16, true);


--
-- Name: migrations_lock_index_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public.migrations_lock_index_seq', 1, true);


--
-- Name: pageHistoryTags_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."pageHistoryTags_id_seq"', 1, false);


--
-- Name: pageHistory_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."pageHistory_id_seq"', 1, false);


--
-- Name: pageLinks_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."pageLinks_id_seq"', 1, false);


--
-- Name: pageTags_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."pageTags_id_seq"', 1, false);


--
-- Name: pagesVector_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."pagesVector_id_seq"', 1, false);


--
-- Name: pages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public.pages_id_seq', 1, false);


--
-- Name: tags_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public.tags_id_seq', 1, false);


--
-- Name: userGroups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."userGroups_id_seq"', 2, true);


--
-- Name: userKeys_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public."userKeys_id_seq"', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: wikijs
--

SELECT pg_catalog.setval('public.users_id_seq', 2, true);


--
-- Name: analytics analytics_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.analytics
    ADD CONSTRAINT analytics_pkey PRIMARY KEY (key);


--
-- Name: apiKeys apiKeys_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."apiKeys"
    ADD CONSTRAINT "apiKeys_pkey" PRIMARY KEY (id);


--
-- Name: assetData assetData_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."assetData"
    ADD CONSTRAINT "assetData_pkey" PRIMARY KEY (id);


--
-- Name: assetFolders assetFolders_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."assetFolders"
    ADD CONSTRAINT "assetFolders_pkey" PRIMARY KEY (id);


--
-- Name: assets assets_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.assets
    ADD CONSTRAINT assets_pkey PRIMARY KEY (id);


--
-- Name: authentication authentication_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.authentication
    ADD CONSTRAINT authentication_pkey PRIMARY KEY (key);


--
-- Name: commentProviders commentProviders_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."commentProviders"
    ADD CONSTRAINT "commentProviders_pkey" PRIMARY KEY (key);


--
-- Name: comments comments_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);


--
-- Name: editors editors_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.editors
    ADD CONSTRAINT editors_pkey PRIMARY KEY (key);


--
-- Name: groups groups_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (id);


--
-- Name: locales locales_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.locales
    ADD CONSTRAINT locales_pkey PRIMARY KEY (code);


--
-- Name: loggers loggers_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.loggers
    ADD CONSTRAINT loggers_pkey PRIMARY KEY (key);


--
-- Name: migrations_lock migrations_lock_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.migrations_lock
    ADD CONSTRAINT migrations_lock_pkey PRIMARY KEY (index);


--
-- Name: migrations migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.migrations
    ADD CONSTRAINT migrations_pkey PRIMARY KEY (id);


--
-- Name: navigation navigation_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.navigation
    ADD CONSTRAINT navigation_pkey PRIMARY KEY (key);


--
-- Name: pageHistoryTags pageHistoryTags_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistoryTags"
    ADD CONSTRAINT "pageHistoryTags_pkey" PRIMARY KEY (id);


--
-- Name: pageHistory pageHistory_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistory"
    ADD CONSTRAINT "pageHistory_pkey" PRIMARY KEY (id);


--
-- Name: pageLinks pageLinks_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageLinks"
    ADD CONSTRAINT "pageLinks_pkey" PRIMARY KEY (id);


--
-- Name: pageTags pageTags_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageTags"
    ADD CONSTRAINT "pageTags_pkey" PRIMARY KEY (id);


--
-- Name: pageTree pageTree_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageTree"
    ADD CONSTRAINT "pageTree_pkey" PRIMARY KEY (id);


--
-- Name: pagesVector pagesVector_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pagesVector"
    ADD CONSTRAINT "pagesVector_pkey" PRIMARY KEY (id);


--
-- Name: pages pages_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.pages
    ADD CONSTRAINT pages_pkey PRIMARY KEY (id);


--
-- Name: renderers renderers_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.renderers
    ADD CONSTRAINT renderers_pkey PRIMARY KEY (key);


--
-- Name: searchEngines searchEngines_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."searchEngines"
    ADD CONSTRAINT "searchEngines_pkey" PRIMARY KEY (key);


--
-- Name: sessions sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.sessions
    ADD CONSTRAINT sessions_pkey PRIMARY KEY (sid);


--
-- Name: settings settings_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (key);


--
-- Name: storage storage_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.storage
    ADD CONSTRAINT storage_pkey PRIMARY KEY (key);


--
-- Name: tags tags_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_pkey PRIMARY KEY (id);


--
-- Name: tags tags_tag_unique; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_tag_unique UNIQUE (tag);


--
-- Name: userAvatars userAvatars_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."userAvatars"
    ADD CONSTRAINT "userAvatars_pkey" PRIMARY KEY (id);


--
-- Name: userGroups userGroups_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."userGroups"
    ADD CONSTRAINT "userGroups_pkey" PRIMARY KEY (id);


--
-- Name: userKeys userKeys_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."userKeys"
    ADD CONSTRAINT "userKeys_pkey" PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_providerkey_email_unique; Type: CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_providerkey_email_unique UNIQUE ("providerKey", email);


--
-- Name: pageWords_idx; Type: INDEX; Schema: public; Owner: wikijs
--

CREATE INDEX "pageWords_idx" ON public."pagesWords" USING gin (word public.gin_trgm_ops);


--
-- Name: pagelinks_path_localecode_index; Type: INDEX; Schema: public; Owner: wikijs
--

CREATE INDEX pagelinks_path_localecode_index ON public."pageLinks" USING btree (path, "localeCode");


--
-- Name: sessions_expired_index; Type: INDEX; Schema: public; Owner: wikijs
--

CREATE INDEX sessions_expired_index ON public.sessions USING btree (expired);


--
-- Name: assetFolders assetfolders_parentid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."assetFolders"
    ADD CONSTRAINT assetfolders_parentid_foreign FOREIGN KEY ("parentId") REFERENCES public."assetFolders"(id);


--
-- Name: assets assets_authorid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.assets
    ADD CONSTRAINT assets_authorid_foreign FOREIGN KEY ("authorId") REFERENCES public.users(id);


--
-- Name: assets assets_folderid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.assets
    ADD CONSTRAINT assets_folderid_foreign FOREIGN KEY ("folderId") REFERENCES public."assetFolders"(id);


--
-- Name: comments comments_authorid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_authorid_foreign FOREIGN KEY ("authorId") REFERENCES public.users(id);


--
-- Name: comments comments_pageid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pageid_foreign FOREIGN KEY ("pageId") REFERENCES public.pages(id);


--
-- Name: pageHistory pagehistory_authorid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistory"
    ADD CONSTRAINT pagehistory_authorid_foreign FOREIGN KEY ("authorId") REFERENCES public.users(id);


--
-- Name: pageHistory pagehistory_editorkey_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistory"
    ADD CONSTRAINT pagehistory_editorkey_foreign FOREIGN KEY ("editorKey") REFERENCES public.editors(key);


--
-- Name: pageHistory pagehistory_localecode_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistory"
    ADD CONSTRAINT pagehistory_localecode_foreign FOREIGN KEY ("localeCode") REFERENCES public.locales(code);


--
-- Name: pageHistoryTags pagehistorytags_pageid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistoryTags"
    ADD CONSTRAINT pagehistorytags_pageid_foreign FOREIGN KEY ("pageId") REFERENCES public."pageHistory"(id) ON DELETE CASCADE;


--
-- Name: pageHistoryTags pagehistorytags_tagid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageHistoryTags"
    ADD CONSTRAINT pagehistorytags_tagid_foreign FOREIGN KEY ("tagId") REFERENCES public.tags(id) ON DELETE CASCADE;


--
-- Name: pageLinks pagelinks_pageid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageLinks"
    ADD CONSTRAINT pagelinks_pageid_foreign FOREIGN KEY ("pageId") REFERENCES public.pages(id) ON DELETE CASCADE;


--
-- Name: pages pages_authorid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.pages
    ADD CONSTRAINT pages_authorid_foreign FOREIGN KEY ("authorId") REFERENCES public.users(id);


--
-- Name: pages pages_creatorid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.pages
    ADD CONSTRAINT pages_creatorid_foreign FOREIGN KEY ("creatorId") REFERENCES public.users(id);


--
-- Name: pages pages_editorkey_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.pages
    ADD CONSTRAINT pages_editorkey_foreign FOREIGN KEY ("editorKey") REFERENCES public.editors(key);


--
-- Name: pages pages_localecode_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.pages
    ADD CONSTRAINT pages_localecode_foreign FOREIGN KEY ("localeCode") REFERENCES public.locales(code);


--
-- Name: pageTags pagetags_pageid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageTags"
    ADD CONSTRAINT pagetags_pageid_foreign FOREIGN KEY ("pageId") REFERENCES public.pages(id) ON DELETE CASCADE;


--
-- Name: pageTags pagetags_tagid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageTags"
    ADD CONSTRAINT pagetags_tagid_foreign FOREIGN KEY ("tagId") REFERENCES public.tags(id) ON DELETE CASCADE;


--
-- Name: pageTree pagetree_localecode_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageTree"
    ADD CONSTRAINT pagetree_localecode_foreign FOREIGN KEY ("localeCode") REFERENCES public.locales(code);


--
-- Name: pageTree pagetree_pageid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageTree"
    ADD CONSTRAINT pagetree_pageid_foreign FOREIGN KEY ("pageId") REFERENCES public.pages(id) ON DELETE CASCADE;


--
-- Name: pageTree pagetree_parent_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."pageTree"
    ADD CONSTRAINT pagetree_parent_foreign FOREIGN KEY (parent) REFERENCES public."pageTree"(id) ON DELETE CASCADE;


--
-- Name: userGroups usergroups_groupid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."userGroups"
    ADD CONSTRAINT usergroups_groupid_foreign FOREIGN KEY ("groupId") REFERENCES public.groups(id) ON DELETE CASCADE;


--
-- Name: userGroups usergroups_userid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."userGroups"
    ADD CONSTRAINT usergroups_userid_foreign FOREIGN KEY ("userId") REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: userKeys userkeys_userid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public."userKeys"
    ADD CONSTRAINT userkeys_userid_foreign FOREIGN KEY ("userId") REFERENCES public.users(id);


--
-- Name: users users_defaulteditor_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_defaulteditor_foreign FOREIGN KEY ("defaultEditor") REFERENCES public.editors(key);


--
-- Name: users users_localecode_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_localecode_foreign FOREIGN KEY ("localeCode") REFERENCES public.locales(code);


--
-- Name: users users_providerkey_foreign; Type: FK CONSTRAINT; Schema: public; Owner: wikijs
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_providerkey_foreign FOREIGN KEY ("providerKey") REFERENCES public.authentication(key);


--
-- PostgreSQL database dump complete
--


```