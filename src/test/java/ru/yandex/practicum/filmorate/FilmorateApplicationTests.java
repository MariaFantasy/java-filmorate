package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@JdbcTest
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@Import({UserDbStorage.class})
class FilmorateApplicationTests {
	private final MpaDbStorage mpaDbStorage;
	private final DirectorDbStorage directorDbStorage;
	private final GenreDbStorage genreDbStorage;
	private final FriendshipDbStorage friendshipDbStorage;
	private final FilmDbStorage filmDbStorage;
	private final UserDbStorage userDbStorage;

	@Test
	public void testFindMpaById() {
		Mpa mpa = mpaDbStorage.findById(1);

		assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
	}

	@Test
	public void testFindAllMpa() {
		Collection<Mpa> mpa = mpaDbStorage.findAll();

        assertEquals(5, mpa.size());
	}

	@Test
	public void testFindGenresById() {
		Genre genre = genreDbStorage.findById(1);

		assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
	}

	@Test
	public void testFindAllGenres() {
		Collection<Genre> genre = genreDbStorage.findAll();

		assertEquals(6, genre.size());
	}

	@Test
	public void testCreateUser() {
		User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));

		User createdUser = userDbStorage.create(user);

		assertThat(createdUser).hasFieldOrPropertyWithValue("email", "myemail@gmail.com");
		assertThat(createdUser).hasFieldOrPropertyWithValue("login", "login");
		assertThat(createdUser).hasFieldOrPropertyWithValue("name", "name");
		assertThat(createdUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2024, 1, 1));
	}

	@Test
	public void testUpdateUser() {
		User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
		User createdUser = userDbStorage.create(user);

		User userToUpdate = new User(createdUser.getId(), "mynewemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
		userDbStorage.update(userToUpdate);

		User updatedUser = userDbStorage.findById(createdUser.getId());

		assertThat(updatedUser).hasFieldOrPropertyWithValue("email", "mynewemail@gmail.com");
		assertThat(updatedUser).hasFieldOrPropertyWithValue("login", "login");
		assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "name");
		assertThat(updatedUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2024, 1, 1));
	}

	@Test
	public void testFindUserById() {
		User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
		User createdUser = userDbStorage.create(user);

		User foundUser = userDbStorage.findById(createdUser.getId());

		assertThat(foundUser).hasFieldOrPropertyWithValue("id", createdUser.getId());
	}

	@Test
	public void testFindAllUsers() {
		User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
		User createdUser = userDbStorage.create(user);

		Collection<User> users = userDbStorage.findAll();

		assertTrue(users.contains(createdUser));
	}

	@Test
	public void testDeleteUser() {
		User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
		User createdUser = userDbStorage.create(user);

		userDbStorage.delete(createdUser);

		Collection<User> users = userDbStorage.findAll();

		assertFalse(users.contains(createdUser));
	}

	@Test
	public void testGetAddAndConfirmFriends() {
		User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
		User createdUser = userDbStorage.create(user);
		User friend = new User(1L, "myfriendemail@gmail.com", "friend_login", "friend_name", LocalDate.of(2024, 1, 1));
		User friendUser = userDbStorage.create(friend);

		friendshipDbStorage.addFriend(user, friend);
		friendshipDbStorage.confirmFriend(user, friend);

		Collection<Long> friends = friendshipDbStorage.getFriends(user);

		assertTrue(friends.contains(friend.getId()));
	}

	@Test
	public void testDeleteFriend() {
		User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
		User createdUser = userDbStorage.create(user);
		User friend = new User(1L, "myfriendemail@gmail.com", "friend_login", "friend_name", LocalDate.of(2024, 1, 1));
		User friendUser = userDbStorage.create(friend);

		friendshipDbStorage.addFriend(user, friend);
		friendshipDbStorage.confirmFriend(user, friend);
		friendshipDbStorage.deleteFriend(user, friend);

		Collection<Long> friends = friendshipDbStorage.getFriends(user);

		assertFalse(friends.contains(friend.getId()));
	}

	@Test
	public void testCreateFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film(1L, "New Era", mpa, new HashSet<Genre>(), new HashSet<Director>(), "BBB", LocalDate.of(2024, 8, 3), 60, 6., new HashSet<>());

		Film createdFilm = filmDbStorage.create(film);

		assertThat(createdFilm).hasFieldOrPropertyWithValue("name", "New Era");
		assertThat(createdFilm).hasFieldOrPropertyWithValue("mpa", mpa);
		assertThat(createdFilm).hasFieldOrPropertyWithValue("description", "BBB");
		assertThat(createdFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2024, 8, 3));
		assertThat(createdFilm).hasFieldOrPropertyWithValue("duration", 60);
	}

	@Test
	public void testUpdateFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film(1L, "New Era", mpa, new HashSet<Genre>(), new HashSet<Director>(), "BBB", LocalDate.of(2024, 8, 3), 60, 6., new HashSet<>());

		Film createdFilm = filmDbStorage.create(film);

		Film filmToUpdate = new Film(createdFilm.getId(), "Old Era", mpa, new HashSet<Genre>(), new HashSet<Director>(), "BBB", LocalDate.of(2024, 8, 3), 60, 6., new HashSet<>());

		Film updatedFilm = filmDbStorage.create(filmToUpdate);

		assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "Old Era");
		assertThat(updatedFilm).hasFieldOrPropertyWithValue("mpa", mpa);
		assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "BBB");
		assertThat(updatedFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2024, 8, 3));
		assertThat(updatedFilm).hasFieldOrPropertyWithValue("duration", 60);
	}

	@Test
	public void testFindByIdFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film(1L, "New Era", mpa, new HashSet<Genre>(), new HashSet<Director>(), "BBB", LocalDate.of(2024, 8, 3), 60, 6., new HashSet<>());
		Film createdFilm = filmDbStorage.create(film);

		Film foundFilm = filmDbStorage.findById(film.getId());

		assertThat(foundFilm).hasFieldOrPropertyWithValue("id", createdFilm.getId());
	}

	@Test
	public void testFindAllFilms() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film(1L, "New Era", mpa, new HashSet<Genre>(), new HashSet<Director>(), "BBB", LocalDate.of(2024, 8, 3), 60, 6., new HashSet<>());
		Film createdFilm = filmDbStorage.create(film);

		Collection<Film> films = filmDbStorage.findAll();

		assertTrue(films.contains(createdFilm));
	}

	@Test
	public void testDeleteFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film(1L, "New Era", mpa, new HashSet<Genre>(), new HashSet<Director>(), "BBB", LocalDate.of(2024, 8, 3), 60, 6., new HashSet<>());
		Film createdFilm = filmDbStorage.create(film);
		filmDbStorage.delete(createdFilm);

		Collection<User> films = userDbStorage.findAll();

		assertFalse(films.contains(createdFilm));
	}

	@Test
	public void testGetFilmsByDirector() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Director director = new Director(1L, "A. A. K.");
		Director createdDirector = directorDbStorage.create(director);
		Film film1 = new Film(1L, "New Era", mpa, new HashSet<Genre>(), Set.of(createdDirector), "BBB", LocalDate.of(2014, 8, 3), 60, 6., new HashSet<>());
		Film film2 = new Film(1L, "New Era 2", mpa, new HashSet<Genre>(), Set.of(createdDirector), "BBB", LocalDate.of(2024, 8, 3), 60, 6., new HashSet<>());
		Film createdFilm1 = filmDbStorage.create(film1);
		Film createdFilm2 = filmDbStorage.create(film2);

		List<Film> films = filmDbStorage.getByDirector(director.getId());

		assertTrue(films.contains(createdFilm1));
		assertTrue(films.contains(createdFilm2));
		assertEquals(2, films.size());
	}

	@Test
	public void testCreateDirector() {
		Director director = new Director(1L, "Guy Ritchie");

		Director createdDirector = directorDbStorage.create(director);

		assertThat(createdDirector).hasFieldOrPropertyWithValue("name", "Guy Ritchie");
	}

	@Test
	public void testUpdateDirector() {
		Director director = new Director(1L, "Guy Ritchie 2");
		Director createdDirector = directorDbStorage.create(director);

		Director directorToUpdate = new Director(createdDirector.getId(), "Quentin Jerome Tarantino");
		Director updatedDirector = directorDbStorage.create(directorToUpdate);

		assertThat(updatedDirector).hasFieldOrPropertyWithValue("name", "Quentin Jerome Tarantino");
	}

	@Test
	public void testFindByIdDirector() {
		Director director = new Director(1L, "Guy Ritchie 3");
		Director createdDirector = directorDbStorage.create(director);

		Director foundDirector = directorDbStorage.findById(createdDirector.getId());

		assertThat(foundDirector).hasFieldOrPropertyWithValue("id", createdDirector.getId());
	}

	@Test
	public void testFindAllDirectors() {
		Director director = new Director(1L, "Guy Ritchie 4");
		Director createdDirector = directorDbStorage.create(director);

		Collection<Director> directors = directorDbStorage.findAll();

		assertTrue(directors.contains(createdDirector));
	}

	@Test
	public void testDeleteDirector() {
		Director director = new Director(1L, "Guy Ritchie 5");
		Director createdDirector = directorDbStorage.create(director);
		directorDbStorage.delete(createdDirector);

		Collection<Director> directors = directorDbStorage.findAll();

		assertFalse(directors.contains(createdDirector));
	}

}
