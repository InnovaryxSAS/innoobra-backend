variable "lambdas" {
  description = "Lambda list"
  type = map(object({
    handler   = string      
    jar_path  = string      
    route_key = string      
    method    = string      
  }))
  default = {
    create_company = {
      handler   = "com.lambdas.handler.CreateCompanyHandler::handleRequest"
      jar_path  = "functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "POST /company"
      method    = "POST"
    },
    get_companies = {
      handler   = "com.lambdas.handler.GetCompaniesHandler::handleRequest"
      jar_path  = "functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "GET /company"
      method    = "POST"
    },
    get_companiesID = {
      handler   = "com.lambdas.handler.GetCompanyByIdHandler::handleRequest"
      jar_path  = "functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "GET /company/{id}"
      method    = "POST"
    },
    update_company = {
      handler   = "com.lambdas.handler.UpdateCompanyHandler::handleRequest"
      jar_path  = "functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "PUT /company/{id}"
      method    = "POST"
    },
    delete_company = {
      handler   = "com.lambdas.handler.DeleteCompanyHandler::handleRequest"
      jar_path  = "functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "DELETE /company/{id}"
      method    = "POST"
    },
    
  }
}
