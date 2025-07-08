variable "environment" {
  description = "Deployment environment (e.g. dev, prod)"
  type        = string
  default     = "dev"
}
variable "lambdas" {
  description = "Lambda list"
  type = map(object({
    handler   = string      
    jar_path  = string      
    route_key = string       
  }))
  default = {
    # === Company Lambdas ===
    create_company = {
      handler   = "com.lambdas.handler.CreateCompanyHandler::handleRequest"
      jar_path  = "functions/company/company-1.0-SNAPSHOT.jar"
      route_key = "POST /company"
    },
    get_companies = {
      handler   = "com.lambdas.handler.GetCompaniesHandler::handleRequest"
      jar_path  = "functions/company/company-1.0-SNAPSHOT.jar"
      route_key = "GET /company"
    },
    get_companiesID = {
      handler   = "com.lambdas.handler.GetCompanyByIdHandler::handleRequest"
      jar_path  = "functions/company/company-1.0-SNAPSHOT.jar"
      route_key = "GET /company/{id}"
    },
    update_company = {
      handler   = "com.lambdas.handler.UpdateCompanyHandler::handleRequest"
      jar_path  = "functions/company/company-1.0-SNAPSHOT.jar"
      route_key = "PUT /company/{id}"
    },
    delete_company = {
      handler   = "com.lambdas.handler.DeleteCompanyHandler::handleRequest"
      jar_path  = "functions/company/company-1.0-SNAPSHOT.jar"
      route_key = "DELETE /company/{id}"
    },

    # === Project Lambdas ===
    create_project = {
    handler   = "com.lambdas.handler.CreateProjectHandler::handleRequest"
    jar_path  = "functions/project/project-1.0-SNAPSHOT.jar"
    route_key = "POST /project"
    },
    get_projects = {
      handler   = "com.lambdas.handler.GetProjectsHandler::handleRequest"
      jar_path  = "functions/project/project-1.0-SNAPSHOT.jar"
      route_key = "GET /project"
    },
    get_project_by_id = {
      handler   = "com.lambdas.handler.GetProjectByIdHandler::handleRequest"
      jar_path  = "functions/project/project-1.0-SNAPSHOT.jar"
      route_key = "GET /project/{id}"
    },
    update_project = {
      handler   = "com.lambdas.handler.UpdateProjectHandler::handleRequest"
      jar_path  = "functions/project/project-1.0-SNAPSHOT.jar"
      route_key = "PUT /project/{id}"
    },
    delete_project = {
      handler   = "com.lambdas.handler.DeleteProjectHandler::handleRequest"
      jar_path  = "functions/project/project-1.0-SNAPSHOT.jar"
      route_key = "DELETE /project/{id}"
    },

    # === Role Lambdas ===

    create_role = {
      handler   = "com.lambdas.handler.CreateRoleHandler::handleRequest"
      jar_path  = "functions/role/role-1.0-SNAPSHOT.jar"
      route_key = "POST /role"
    },
    get_roles = {
      handler   = "com.lambdas.handler.GetRolesHandler::handleRequest"
      jar_path  = "functions/role/role-1.0-SNAPSHOT.jar"
      route_key = "GET /role"
    },
    get_role_by_id = {
      handler   = "com.lambdas.handler.GetRoleByIdHandler::handleRequest"
      jar_path  = "functions/role/role-1.0-SNAPSHOT.jar"
      route_key = "GET /role/{id}"
    },
    update_role = {
      handler   = "com.lambdas.handler.UpdateRoleHandler::handleRequest"
      jar_path  = "functions/role/role-1.0-SNAPSHOT.jar"
      route_key = "PUT /role/{id}"
    },
    delete_role = {
      handler   = "com.lambdas.handler.DeleteRoleHandler::handleRequest"
      jar_path  = "functions/role/role-1.0-SNAPSHOT.jar"
      route_key = "DELETE /role/{id}"
    },

    # === Others Lambdas ===
    
  }
}
