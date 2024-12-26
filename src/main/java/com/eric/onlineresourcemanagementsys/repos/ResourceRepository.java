package com.eric.onlineresourcemanagementsys.repos;

import com.eric.onlineresourcemanagementsys.Entity.Resource;
import com.eric.onlineresourcemanagementsys.Entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends GenericRepository<Resource, Integer> {
    List<Resource> findAllByUserId(int userId);
    Resource findResourceByNameAndUser(String resourceName, User user);
    Resource findResourceById(int resourceId);
}
