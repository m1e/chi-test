drop schema graph;

create schema graph
  authorization postgres;

create extension if not exists ltree;
drop table if exists nodes;


/*https://www.postgresql.org/docs/9.1/static/ltree.html*/
create table nodes(
    id text primary key,
    path ltree
);

/*https://www.postgresql.org/docs/9.1/static/textsearch-indexes.html*/
create index if not exists tree_path_idx on nodes (path);