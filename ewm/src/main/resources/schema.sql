CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL   VARCHAR(254) UNIQUE NOT NULL,
    NAME    VARCHAR(250)        NOT NULL
);

CREATE TABLE IF NOT EXISTS CATEGORY
(
    CATEGORY_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME        VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS LOCATION
(
    LOCATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    LATITUDE    DECIMAL NOT NULL,
    LONGITUDE   DECIMAL NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    EVENT_ID           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ANNOTATION         VARCHAR(2000) NOT NULL,
    CATEGORY_ID        BIGINT        NOT NULL,
    CREATED_ON         TIMESTAMP     NOT NULL,
    DESCRIPTION        VARCHAR(7000) NOT NULL,
    EVENT_DATE         TIMESTAMP     NOT NULL,
    INITIATOR_ID       BIGINT        NOT NULL,
    LOCATION_ID        BIGINT        NOT NULL,
    PAID               BOOLEAN       NOT NULL,
    PARTICIPANT_LIMIT  INTEGER       NOT NULL,
    PUBLISHED_ON       TIMESTAMP,
    REQUEST_MODERATION BOOLEAN       NOT NULL,
    STATE              VARCHAR(15)   NOT NULL,
    TITLE              VARCHAR(120)  NOT NULL,

    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY (CATEGORY_ID) ON DELETE RESTRICT,
    FOREIGN KEY (INITIATOR_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    FOREIGN KEY (LOCATION_ID) REFERENCES LOCATION (LOCATION_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    REQUEST_ID   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EVENT_ID     BIGINT      NOT NULL,
    REQUESTER_ID BIGINT      NOT NULL,
    STATUS       VARCHAR(15) NOT NULL,
    CREATED      TIMESTAMP   NOT NULL,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE,
    FOREIGN KEY (REQUESTER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    COMPILATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    PINNED         BOOLEAN      NOT NULL,
    TITLE          VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENT_COMPILATION
(
    EVENT_ID       BIGINT,
    compilation_id BIGINT NOT NULL,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE,
    FOREIGN KEY (COMPILATION_ID) REFERENCES COMPILATIONS (COMPILATION_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    COMMENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    TEXT       VARCHAR(255) NOT NULL,
    EVENT_ID   INTEGER      NOT NULL,
    AUTHOR_ID  INTEGER      NOT NULL,
    CREATED    TIMESTAMP    NOT NULL,
    foreign key (AUTHOR_ID) references USERS (USER_ID) ON DELETE CASCADE
);