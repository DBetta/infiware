package ke.co.infiware.uaa.useradministration.repositories

import ke.co.infiware.uaa.useradministration.enities.InfiwareUser
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Denis Gitonga
 */
interface UserRepository : JpaRepository<InfiwareUser, String>  {

}