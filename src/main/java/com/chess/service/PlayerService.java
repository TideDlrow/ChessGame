package com.chess.service;

import com.chess.bean.Player;
import com.chess.dao.PlayerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("playerService")
public class PlayerService {
    @Autowired
    PlayerDAO playerDAO;

    public List<Player> getAll(){
        return playerDAO.getAll();
    }

    public Player getPlayerById(int id){
        return playerDAO.getPlayerById(id);
    }

    public Player getPlayerByName(String userName){
        return playerDAO.getPlayerByName(userName);
    }

    public void delete(int id){
        playerDAO.delete(id);
    }

    public Player addPlayer(Player player){
         return playerDAO.addPlayer(player);
    }

    public int verifyPassword(Player player) {
        return playerDAO.verifyPassword(player);
    }

    public int getPlayerNameNum(String userName){
        return playerDAO.getPlayerNameNum(userName);
    }
    public void setActiveUUID(int id,String uuid){
        playerDAO.setActiveUUID(id, uuid);
    }
}
