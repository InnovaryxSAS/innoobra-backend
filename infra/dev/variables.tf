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
      handler   = "com.innobra.lambda.Handler::handleRequest"
      jar_path  = "${path.module}/company/createCompanyHandler.jar"
      route_key = "POST /company"
      method    = "POST"
    },
    get_companies = {
      handler   = "com.tubanco.company.getCompaniesHandler::handleRequest"
      jar_path  = "${path.module}/company/getCompaniesHandler.jar"
      route_key = "GET /company"
      method    = "POST"
    }
  }
}
