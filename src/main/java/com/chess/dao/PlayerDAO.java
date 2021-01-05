package com.chess.dao;

import com.chess.bean.Player;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PlayerDAO {

    List<Player> getAll();

    Player getPlayerById(int id);

    Player getPlayerByName(String userName);

    void delete(int id);

    int addPlayer(Player player);

    int verifyPassword(Player player);

    int getPlayerNameNum(String userName);

    void setActiveUUID(int id,String activeUUID);
}
