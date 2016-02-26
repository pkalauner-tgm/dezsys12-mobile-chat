package at.kalauner.dezsys12.server.db;

import org.springframework.data.repository.CrudRepository;


/**
 * CRUD Operations for User
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160212.1
 */
public interface UserRepository extends CrudRepository<User, String> {
}