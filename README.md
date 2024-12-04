# java-filmorate
Template repository for Filmorate project.

---

## DataBase

```mermaid
erDiagram
    genre {
        integer genre_id PK
        string name UK
    }

    film_genre {
        bigint film_id FK
        integer genre_id FK
    }

    film {
        bigint film_id PK
        string name
        string(200) description "Nullable"
        date release_date
        integer duration
        integer rating_id FK "Nullable"
    }

    rating {
        integer rating_id PK
        varchar name UK 
    }

    film_like {
        bigint film_id FK
        bigint user_id FK
    }

    user {
        bigint user_id PK
        varchar email
        varchar login
        varchar name "Nullable"
        date birthday
    }

    user_friend {
        bigint user_id FK
        bigint friend_id FK
        integer friendship_status_id FK
    }

    friendship_status {
        integer friendship_status_id PK
        varchar name UK
    }

    film_review {
        integer review_id PK
        bigint film_id FK
        bigint user_id FK
        varchar(200) content
        boolean is_positive
        integer useful
    }
    film_review_like {
        bigint review_id FK
        bigint user_id FK
        integer like_value
    }


    genre ||--o{ film_genre : genre_id
    film ||--o{ film_genre : film_id
    rating ||--o{ film : rating_id
    film ||--o{ film_like : film_id
    user ||--o{ film_like : user_id
    user ||--o{ user_friend : user_id
    user ||--o{ user_friend : friend_id
    friendship_status ||--o{ user_friend : friendship_status_id
    film ||--o{ film_review : film_id
    user ||--o{ film_review : user_id
    film_review ||--o{ film_review_like : review_id
    user ||--o{ film_review_like : user_id
```

#### Database Main Queries

1. Get popular films
```sql
WITH top_films AS (
    SELECT
        f.film_id,
        COUNT(user_id) AS cnt 
    FROM film AS f
    INNER JOIN film_like AS fl
        ON f.film_id = fl.film_id
    GROUP BY f.film_id
    ORDER BY cnt DESC
    LIMIT {top_count}
)
SELECT
    f.*
FROM film AS f
INNER JOIN top_films AS t
    ON f.film_id = t.film_id
```
2. Get intersection of friends
```sql
SELECT f.*
FROM user AS f

INNER JOIN user_friend AS user1_fl
    ON f.user_id = user1_fl.friend_id
    AND user1_fl.user_id = {user1}
INNER JOIN friendship_status AS user1_fl_status
    ON user1_fl.friendship_status_id = user1_fl_status.friendship_status_id
    AND user1_fl_status.name = 'ACCEPTED'


INNER JOIN user_friend AS user2_fl
    ON f.user_id = user2_fl.friend_id
    AND user2_fl.user_id = {user2}
INNER JOIN friendship_status AS user2_fl_status
    ON user2_fl.friendship_status_id = user2_fl_status.friendship_status_id
    AND user2_fl_status.name = 'ACCEPTED'
```
