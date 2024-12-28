package com.eric.onlineresourcemanagementsys.controllers;

import com.eric.onlineresourcemanagementsys.Entity.GameAccount;
import com.eric.onlineresourcemanagementsys.Entity.Resource;
import com.eric.onlineresourcemanagementsys.Entity.Subscription;
import com.eric.onlineresourcemanagementsys.Entity.User;
import com.eric.onlineresourcemanagementsys.services.ResourceService;
import com.eric.onlineresourcemanagementsys.services.ServiceResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {
    private final ResourceService resourceService;

    public DashboardController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // redirect to login if no user is logged in
        }
        String username = ((User)user).getUsername().toUpperCase();
        model.addAttribute("username", username );
        return "user/dashboard";
    }

    @GetMapping("/resources")
    public String viewResources(HttpSession session, Model model) {
        Object loggedInUser = session.getAttribute("user");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        User user = (User)loggedInUser;

        ServiceResponse<Map<String, List<Resource>>> response =
                resourceService.groupResourcesByType(user.getId());

        // Check if the response is successful
        if (!response.isSuccess()) {
            // Add an error message to the model if the response failed
            model.addAttribute("error", response.getMessage());
            return "user/resources"; // Render the page even with errors
        }

        String username = user.getUsername().toUpperCase();
        model.addAttribute("groupedResources", response.getData());
        model.addAttribute("username", username );
        return "user/resources";
    }

    @GetMapping("resources/delete/{id}")
    public String deleteResource(@PathVariable int id) {
        resourceService.deleteResource(id);
        return "redirect:/resources";
    }

    @GetMapping("resources/edit/{id}")
    public String editResourceForm(@PathVariable int id, Model model) {
        ServiceResponse<Resource> response = resourceService.getResourceById(id);

        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "redirect:/resources";
        }
        model.addAttribute("resource", response.getData());
        return "user/resource-edit";
    }

    @PostMapping("resources/edit/{id}")
    public String editResource(
            @PathVariable("id") int id,
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "gamePlatform", required = false) String gamePlatform,
            @RequestParam(value = "subscriptionType", required = false) String subscriptionType,
            Model model
    ){
        // retrieve the resource by ID
        ServiceResponse<Resource> findResponse = resourceService.getResourceById(id);
        if (!findResponse.isSuccess()) {
            model.addAttribute("error", findResponse.getMessage());
            return "redirect:/resources";
        }

        Resource resource =findResponse.getData();
        resource.setName(name);
        resource.setUsername(username);
        resource.setPassword(password);
        resource.setPassword(password);

        // Handle specific fields based on the resource type
        if (resource instanceof GameAccount gameAccount) {
            gameAccount.setGamePlatform(gamePlatform);
        } else if (resource instanceof Subscription subscription) {
            subscription.setSubscriptionType(subscriptionType);
        }

        // Save the updated resource
        ServiceResponse<Resource> saveResponse = resourceService.saveResource(resource);
        if (!saveResponse.isSuccess()) {
            model.addAttribute("error", saveResponse.getMessage());
            return "user/resource-edit";
        }

        model.addAttribute("success", "Resource updated successfully!");
        return "redirect:/resources";
    }


    @GetMapping("resources/add-resource")
    public String addResourceForm() {
        return "user/resource-add";
    }

    @PostMapping("resources/add-resource")
    public String addResource(@RequestParam String name,
                              @RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String resourceType,
                              @RequestParam(value = "gamePlatform", required = false) String gamePlatform,
                              @RequestParam(value = "subscriptionType", required = false) String subscriptionType,
                              HttpSession session,
                              Model model
                              ) {

        Object loggedInUser = session.getAttribute("user");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        User user = (User)loggedInUser;

        Resource resource;
        if ("GameAccount".equals(resourceType)) {
            resource = new GameAccount(name, username, password, gamePlatform);
        } else if ("Subscription".equals(resourceType)) {
            resource = new Subscription(name, username, password, subscriptionType);
        } else {
            model.addAttribute("error", "Invalid resource type selected.");
            return "user/resource-add";
        }

        // save the resource
        ServiceResponse<Resource> response = resourceService.addResource(resource, user);
        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "user/resource-add";
        }

        return "redirect:/resources";
    }

}
