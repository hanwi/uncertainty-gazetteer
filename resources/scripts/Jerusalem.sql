create schema jerusalem;

create table jerusalem.authors(author_id integer not null generated always as identity (start with 1, increment by 1), 
name varchar(1000), origin varchar(1000), religious_denomination varchar(1000), begin_year integer default null, 
end_year integer default null, notes varchar(1000), primary key(author_id)); 

create table jerusalem.documents(document_id integer not null generated always as identity (start with 1, increment by 1), 
author_id integer default null, title varchar(1000), begin_year integer default null, end_year integer default null, 
inner_count varchar(1000), outer_count_edition varchar(1000), outer_count_german varchar(1000), outer_count_english varchar(1000), 
outer_count_other varchar(1000), genre varchar(1000), properties varchar(1000), notes varchar(1000), trust integer not null, primary key(document_id)); 

create table jerusalem.entries(entry_id integer not null generated always as identity (start with 1, increment by 1), 
document_id integer not null, inner_count varchar(1000), outer_count_edition varchar(1000), outer_count_german varchar(1000), 
outer_count_english varchar(1000), outer_count_other varchar(1000), notes varchar(1000), primary key(entry_id)); 

create table jerusalem.topos_in_entry(topos_in_entry_id  integer not null generated always as identity (start with 1, increment by 1), 
entry_id integer not null, topos_id integer not null, place_id integer default null, topos_name_in_entry varchar(1000), 
topos_situation varchar(1000), traditions varchar(1000), additional_information varchar(1000), number integer default null, 
notes varchar(1000), primary key(topos_in_entry_id)); 

create table jerusalem.topoi(topos_id integer not null generated always as identity (start with 1, increment by 1), 
name varchar(1000), notes varchar(1000), primary key(topos_id)); 

create table jerusalem.places(place_id integer not null generated always as identity (start with 1, increment by 1), 
name varchar(1000), begin_year integer default null, end_year integer default null, srid varchar(1000), location_easting double not null, 
location_northing double not null,  simple_feature long varchar, tavo_bb varchar(1000), notes varchar(1000), primary key(place_id)); 

create table jerusalem.placetopos(place_id integer not null, topos_id integer not null, begin_year integer default null, end_year integer default null); 

alter table jerusalem.documents add constraint fk_documents_author_id foreign key(author_id) references jerusalem.authors(author_id) on update restrict on delete restrict; 

alter table jerusalem.entries add constraint fk_entries_document_id foreign key(document_id) references jerusalem.documents(document_id) on update restrict on delete restrict; 

alter table jerusalem.topos_in_entry add constraint jerusalem.fk_topos_in_entry_entry_id foreign key(entry_id) references jerusalem.entries(entry_id) on update restrict on delete restrict; 

alter table jerusalem.topos_in_entry add constraint jerusalem.fk_topos_in_entry_topos_id foreign key(topos_id) references jerusalem.topoi(topos_id) on update restrict on delete restrict; 

alter table jerusalem.topos_in_entry add constraint jerusalem.fk_topos_in_entry_place_id foreign key(place_id) references jerusalem.places(place_id) on update restrict on delete restrict; 

alter table jerusalem.placetopos add constraint fk_placetopos_place_id foreign key(place_id) references jerusalem.places(place_id) on update restrict on delete restrict; 

alter table jerusalem.placetopos add constraint fk_placetopos_topos_id foreign key(topos_id) references jerusalem.topoi(topos_id) on update restrict on delete restrict;