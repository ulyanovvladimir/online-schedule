# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table admin (
  id                        integer not null,
  username                  varchar(255),
  userpass                  varchar(255),
  constraint pk_admin primary key (id))
;

create table lesson (
  id                        integer not null,
  group_number              varchar(255),
  day                       varchar(255),
  hours                     varchar(255),
  lecture                   varchar(255),
  teacher                   varchar(255),
  room                      varchar(255),
  constraint pk_lesson primary key (id))
;

create table schedule_url (
  id                        integer not null,
  url                       varchar(255),
  constraint pk_schedule_url primary key (id))
;

create table url (
  id                        integer not null,
  url                       varchar(255),
  constraint pk_url primary key (id))
;

create sequence admin_seq;

create sequence lesson_seq;

create sequence schedule_url_seq;

create sequence url_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists admin;

drop table if exists lesson;

drop table if exists schedule_url;

drop table if exists url;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists admin_seq;

drop sequence if exists lesson_seq;

drop sequence if exists schedule_url_seq;

drop sequence if exists url_seq;

