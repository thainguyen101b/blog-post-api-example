CREATE TABLE category
(
    id   UUID         NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE comment
(
    id           UUID         NOT NULL,
    content      VARCHAR(255) NOT NULL,
    commenter_id VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    approved_at  TIMESTAMP WITHOUT TIME ZONE,
    post_id      UUID,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

CREATE TABLE post
(
    id           UUID         NOT NULL,
    title        VARCHAR(255) NOT NULL,
    content      TEXT,
    author_id    VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    slug         VARCHAR(255),
    published_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    deleted_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_post PRIMARY KEY (id)
);

CREATE TABLE post_category
(
    category_id UUID NOT NULL,
    post_id     UUID NOT NULL
);

ALTER TABLE post
    ADD CONSTRAINT uc_post_slug UNIQUE (slug);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES post (id);

ALTER TABLE post_category
    ADD CONSTRAINT fk_poscat_on_category_entity FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE post_category
    ADD CONSTRAINT fk_poscat_on_post_entity FOREIGN KEY (post_id) REFERENCES post (id);