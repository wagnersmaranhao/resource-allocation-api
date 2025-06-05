# Exercise Statement: Resource Allocation API

## Objective

Build a REST API using Java and the Spring Framework to manage the allocation of resources to projects. The system should allow the management of Projects, Resources, and Positions within a project, enabling the flexible allocation of resources to positions.

## Functional Requirements
1. CRUD for Projects:
   - The system must allow creating, reading, updating, and deleting projects.
   - Each project has a name, a description, and a list of positions to allocate resources.
   - Deletion will be logical, meaning that the project won't be removed from the database, but marked as inactive.

2. CRUD for Resources:
   - The system must allow creating, reading, updating, and deleting resources.
   - Each resource has a name, a role, and availability (boolean, indicating whether the resource is available or not).
   - Deletion will be logical, meaning that the resource won't be removed from the database, but marked as inactive.
3. Positions in Projects:
   - Each project can have multiple positions. Each position represents a need to allocate a resource to the project.
   - Each position can be in one of the following states:
       - Open: no resource is allocated yet.
       - Confirmed: a resource is allocated, and the allocation is confirmed.
       - Unconfirmed: a resource is allocated, but the allocation is not confirmed.
   - A position will also store the start date and end date of the allocation.
   - A single resource can be allocated to multiple positions across different projects.

4. Business Rules:
   - A resource can be assigned to multiple positions and projects simultaneously.
   - When allocating a resource to a position, it can be in Confirmed or Unconfirmed status.
   - It must be possible to list all positions (and their statuses) within a project.
   - It must be possible to list all positions allocated to a resource.
   - Removing a resource from a position will set an end date for that allocation.

## Expected Endpoints
Open the yaml file in [Swagger](https://editor-next.swagger.io/)



## Implementation Guide
1. Class Structure
The following entities are needed:
   - Project: Contains attributes like id, name, description, a list of positions, and a boolean field for logical deletion to mark whether the project is active or inactive.
   - Resource: Contains attributes like id, name, role, availability, a list of positions (allocations), and a boolean field for logical deletion to mark whether the resource is active or inactive.
   - Position: Represents a position within a project. Each position holds:
       - A reference to the project.
       - A reference to the allocated resource (if any).
       - An allocation status (Confirmed or Unconfirmed).
       - Start date and end date for the allocation.
       - A status to indicate whether the position is open (without a resource) or filled (with a resource).

2. Repositories
   - Create repositories for Project, Resource, and Position entities using JpaRepository to handle data persistence. Ensure the repositories handle logical deletion and the proper management of positions and resources.

3. Services
Implement service classes to contain the business logic for managing projects, resources, and positions. The services should handle:
   - Soft deletion of projects and resources by marking them as inactive.
   - Creation and update of positions, including the allocation or deallocation of resources.
   - Handling start date and end date for allocations.
4. Controllers
   - Create REST controllers to expose the API endpoints. The controllers will interact with the service layer to perform operations based on the received HTTP requests, ensuring soft deletions and allocation management.
5. Testing
   - Write unit tests for each service using JUnit and Mockito, including tests for logical deletion and allocation management (start and end dates).
   - Write integration tests for controllers using MockMvc to ensure the correct behavior of the API.
6. Extras
   - Implement pagination for listing projects and resources (Pageable in Spring).
   - Implement exception handling using @ControllerAdvice.
   - Document the API using Swagger to make the endpoints easier to understand and test.
7. Nice to have
   - Place your application behind a proxy in Apigee. Create an authentication flow for all the /activities endpoints. You should require the user to authenticate with Google authentication
   - Structure your postman workspace in the same way as the Swagger document. Create tests to validate your requests.
8. Phase 2
   - Upload a file to GCP(/resource/{id}/cv).
The file must be submited by the user and uploaded to a bucket in the GCP project.
   - Implement role based access to endpoints with Spring Security.
Creativity is allowed, but for reference roles like "Admin" should have full access, roles like "Moderator" should be more restricted, and roles of type "User" should only have access to certain endpoints
   - Integrate a call to an external API, implementing some sort of logic with data obtained from this call.