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
  group_number              varchar(255),
  day                       varchar(255),
  hours                     varchar(255),
  lecture                   varchar(255),
  teacher                   varchar(255),
  room                      varchar(255))
;

create sequence admin_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists admin;

drop table if exists lesson;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists admin_seq;

