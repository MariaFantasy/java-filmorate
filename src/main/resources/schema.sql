CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS rating (
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    rating_id INTEGER,
    FOREIGN KEY (rating_id) REFERENCES rating(rating_id),
    CONSTRAINT positive_duration CHECK (duration > 0),
    CONSTRAINT min_release_date CHECK (release_date >= '1895-12-28'::DATE)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL,
    genre_id INTEGER NOT NULL,
    FOREIGN KEY (film_id) REFERENCES film(film_id),
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id),
    CONSTRAINT film_genre_PK PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS friendship_status (
    friendship_status_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    name VARCHAR,
    birthday DATE NOT NULL,
    CONSTRAINT email_contains_at_symbol CHECK (email like '%@%'),
    CONSTRAINT login_not_contains_spaces CHECK (login not like '% %'),
    CONSTRAINT birthday_in_past CHECK (birthday <= current_date())
);

CREATE TABLE IF NOT EXISTS user_friend (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    friendship_status_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (friendship_status_id) REFERENCES friendship_status(friendship_status_id),
    CONSTRAINT user_friend_PK PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_like (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES film(film_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT film_like_PK PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS director (
    director_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_director (
    film_id BIGINT NOT NULL,
    director_id BIGINT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES film(film_id),
    FOREIGN KEY (director_id) REFERENCES director(director_id),
    CONSTRAINT film_director_PK PRIMARY KEY (film_id, director_id)
);