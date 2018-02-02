create schema graph;

create extension ltree;

/*please see https://www.postgresql.org/docs/9.1/static/ltree.html */
create table nodes(
    id text primary key,
    path ltree
);

/* please see https://www.postgresql.org/docs/9.1/static/textsearch-indexes.html*/
create index tree_path_idx on nodes (path) ;