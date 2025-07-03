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
      jar_path  = "${path.module}/dev/functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "POST /company"
      method    = "POST"
    },
    get_companies = {
      handler   = "com.lambdas.handler.GetCompaniesHandler::handleRequest"
      jar_path  = "${path.module}/dev/functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "GET /company"
      method    = "GET"
    },
    get_companiesID = {
      handler   = "com.lambdas.handler.GetCompanyByIdHandler::handleRequest"
      jar_path  = "${path.module}/dev/functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "GET /company/{id}"
      method    = "GET"
    },
    update_company = {
      handler   = "com.lambdas.handler.UpdateCompanyHandler::handleRequest"
      jar_path  = "${path.module}/dev/functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "PUT /company/{id}"
      method    = "PUT"
    },
    delete_company = {
      handler   = "com.lambdas.handler.DeleteCompanyHandler::handleRequest"
      jar_path  = "${path.module}/dev/functions/company/lambdas-1.0-SNAPSHOT.jar"
      route_key = "DELETE /company/{id}"
      method    = "DELETE"
    },
    
  }
}
