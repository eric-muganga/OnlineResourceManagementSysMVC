package com.eric.onlineresourcemanagementsys.services;

import com.eric.onlineresourcemanagementsys.Entity.Resource;
import com.eric.onlineresourcemanagementsys.Entity.User;
import com.eric.onlineresourcemanagementsys.repos.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }


    // Add a new resource
    public ServiceResponse<Resource> addResource(Resource resource, User user) {
        try {
            resource.setUser(user);
            Resource savedResource = resourceRepository.save(resource);
            return new ServiceResponse<>(savedResource, true, "Resource added successfully", null);
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            return new ServiceResponse<>(null, false, "Failed to add resource", errors);
        }
    }

    // Delete a resource
    public ServiceResponse<Void> deleteResource(int id) {
        try{
            resourceRepository.deleteById(id);
            return new ServiceResponse<>(null, true, "Resource deleted successfully", null);
        }catch (Exception e){
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            return new ServiceResponse<>(null, false, "Failed to delete resource", errors);
        }
    }

    // Find a resource by name and user
    public ServiceResponse<Resource> findResourceByNameAndUser(String resourceName, User user) {
        try{
            Resource savedResource = resourceRepository.findResourceByNameAndUser(resourceName, user);
            if(savedResource != null){
                return new ServiceResponse<>(savedResource, true, "Resource found successfully", null);
            }else {
                return new ServiceResponse<>(null, false, "Resource not found", null);
            }
        }catch (Exception e){
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            return new ServiceResponse<>(null, false, "Failed to find resource", errors);
        }
    }

    public ServiceResponse<Resource> saveResource(Resource resource) {
        try{
            Resource savedResource = resourceRepository.save(resource);
            return new ServiceResponse<>(savedResource, true, "Resource added successfully", null);
        }catch (Exception e){
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            return new ServiceResponse<>(null, false, "Failed to save resource", errors);
        }
    }

    // Retrieve all resources for a user grouping Resources By Type
    public ServiceResponse<Map<String, List<Resource>>>     groupResourcesByType(int userId) {
        try {
            List<Resource> resources = resourceRepository.findAllByUserId(userId);

            if (resources.isEmpty()) {
                return new ServiceResponse<>(null, false, "No resources found for user", null);
            }

            Map<String, List<Resource>> groupedResources = resources.stream()
                    .collect(Collectors.groupingBy(resource -> resource.getClass().getSimpleName()));

            return new ServiceResponse<>(groupedResources, true, "Resources grouped successfully", null);
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            return new ServiceResponse<>(null, false, "Failed to group resources", errors);
        }
    }


    public ServiceResponse<Resource> getResourceById(int id) {
        try {
            Resource theResource = resourceRepository.findResourceById(id);
            if(theResource != null){
                return new ServiceResponse<>(theResource, true, "Resource found successfully", null);
            } else {
                return new ServiceResponse<>(null, false, "Resource not found", null);
            }
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            return new ServiceResponse<>(null, false, "Failed to get resource", errors);
        }
    }
}
