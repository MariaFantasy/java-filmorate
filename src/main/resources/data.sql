MERGE INTO genre (genre_id, name)
VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

MERGE INTO rating (rating_id, name)
VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

MERGE INTO friendship_status (friendship_status_id, name)
VALUES
    (1, 'Accept'),
    (2, 'Waiting Accept');
