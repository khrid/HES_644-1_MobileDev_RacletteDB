package ch.hevs.students.raclettedb.database.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import ch.hevs.students.raclettedb.database.entity.AccountEntity;
import ch.hevs.students.raclettedb.database.entity.ClientEntity;

/**
 * https://developer.android.com/reference/android/arch/persistence/room/Relation
 */
public class ClientWithAccounts {
    @Embedded
    public ClientEntity client;

    @Relation(parentColumn = "email", entityColumn = "owner", entity = AccountEntity.class)
    public List<AccountEntity> accounts;
}