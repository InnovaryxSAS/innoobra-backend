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
    get_company = {
      handler   = "com.lambdas.handler.GetCompanyHandler::handleRequest"
      jar_path  = "functions/company/company-1.0-SNAPSHOT.jar"
      route_key = "GET /company"
    },
    get_company_by_id = {
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
    get_project = {
      handler   = "com.lambdas.handler.GetProjectHandler::handleRequest"
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
    get_role = {
      handler   = "com.lambdas.handler.GetRoleHandler::handleRequest"
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

    # === User Lambdas ===

    create_user = {
      handler   = "com.lambdas.handler.CreateUserHandler::handleRequest"
      jar_path  = "functions/user/user-1.0-SNAPSHOT.jar"
      route_key = "POST /user"
    },
    get_user = {
      handler   = "com.lambdas.handler.GetUserHandler::handleRequest"
      jar_path  = "functions/user/user-1.0-SNAPSHOT.jar"
      route_key = "GET /user"
    },
    get_user_by_id = {
      handler   = "com.lambdas.handler.GetUserByIdHandler::handleRequest"
      jar_path  = "functions/user/user-1.0-SNAPSHOT.jar"
      route_key = "GET /user/{id}"
    },
    update_user = {
      handler   = "com.lambdas.handler.UpdateUserHandler::handleRequest"
      jar_path  = "functions/user/user-1.0-SNAPSHOT.jar"
      route_key = "PUT /user/{id}"
    },
    delete_user = {
      handler   = "com.lambdas.handler.DeleteUserHandler::handleRequest"
      jar_path  = "functions/user/user-1.0-SNAPSHOT.jar"
      route_key = "DELETE /user/{id}"
    },

    # === Chapters Lambdas ===

    create_chapter = {
      handler   = "com.lambdas.handler.CreateChapterHandler::handleRequest"
      jar_path  = "functions/chapter/chapter-1.0-SNAPSHOT.jar"
      route_key = "POST /chapter"
    },
    get_chapter = {
      handler   = "com.lambdas.handler.GetChapterHandler::handleRequest"
      jar_path  = "functions/chapter/chapter-1.0-SNAPSHOT.jar"
      route_key = "GET /chapter"
    },
    get_chapter_by_id = {
      handler   = "com.lambdas.handler.GetChapterByIdHandler::handleRequest"
      jar_path  = "functions/chapter/chapter-1.0-SNAPSHOT.jar"
      route_key = "GET /chapter/{id}"
    },
    update_chapter = {
      handler   = "com.lambdas.handler.UpdateChapterHandler::handleRequest"
      jar_path  = "functions/chapter/chapter-1.0-SNAPSHOT.jar"
      route_key = "PUT /chapter/{id}"
    },
    delete_chapter = {
      handler   = "com.lambdas.handler.DeleteChapterHandler::handleRequest"
      jar_path  = "functions/chapter/chapter-1.0-SNAPSHOT.jar"
      route_key = "DELETE /chapter/{id}"
    },

     # === Activity Lambdas ===

    create_activity = {
      handler   = "com.lambdas.handler.CreateActivityHandler::handleRequest"
      jar_path  = "functions/activity/activity-1.0-SNAPSHOT.jar"
      route_key = "POST /activity"
    },
    get_activity = {
      handler   = "com.lambdas.handler.GetActivityHandler::handleRequest"
      jar_path  = "functions/activity/activity-1.0-SNAPSHOT.jar"
      route_key = "GET /activity"
    },
    get_activity_by_id = {
      handler   = "com.lambdas.handler.GetActivityByIdHandler::handleRequest"
      jar_path  = "functions/activity/activity-1.0-SNAPSHOT.jar"
      route_key = "GET /activity/{id}"
    },
    update_activity = {
      handler   = "com.lambdas.handler.UpdateActivityHandler::handleRequest"
      jar_path  = "functions/activity/activity-1.0-SNAPSHOT.jar"
      route_key = "PUT /activity/{id}"
    },
    delete_activity = {
      handler   = "com.lambdas.handler.DeleteActivityHandler::handleRequest"
      jar_path  = "functions/activity/activity-1.0-SNAPSHOT.jar"
      route_key = "DELETE /activity/{id}"
    },

    # === Attribute Lambdas ===

    create_attribute = {
      handler   = "com.lambdas.handler.CreateAttributeHandler::handleRequest"
      jar_path  = "functions/attribute/attribute-1.0-SNAPSHOT.jar"
      route_key = "POST /attribute"
    },
    get_attribute = {
      handler   = "com.lambdas.handler.GetAttributeHandler::handleRequest"
      jar_path  = "functions/attribute/attribute-1.0-SNAPSHOT.jar"
      route_key = "GET /attribute"
    },
    get_attribute_by_id = {
      handler   = "com.lambdas.handler.GetAttributeByIdHandler::handleRequest"
      jar_path  = "functions/attribute/attribute-1.0-SNAPSHOT.jar"
      route_key = "GET /attribute/{id}"
    },
    update_attribute = {
      handler   = "com.lambdas.handler.UpdateAttributeHandler::handleRequest"
      jar_path  = "functions/attribute/attribute-1.0-SNAPSHOT.jar"
      route_key = "PUT /attribute/{id}"
    },
    delete_attribute = {
      handler   = "com.lambdas.handler.DeleteAttributeHandler::handleRequest"
      jar_path  = "functions/attribute/attribute-1.0-SNAPSHOT.jar"
      route_key = "DELETE /attribute/{id}"
    },

    # === APU_DETAIL Lambdas ===

    create_apudetail = {
      handler   = "com.lambdas.handler.CreateApuDetailHandler::handleRequest"
      jar_path  = "functions/apudetail/apudetail-1.0-SNAPSHOT.jar"
      route_key = "POST /apudetail"
    },
    get_apudetail = {
      handler   = "com.lambdas.handler.GetApuDetailHandler::handleRequest"
      jar_path  = "functions/apudetail/apudetail-1.0-SNAPSHOT.jar"
      route_key = "GET /apudetail"
    },
    get_apudetail_by_id = {
      handler   = "com.lambdas.handler.GetApuDetailByIdHandler::handleRequest"
      jar_path  = "functions/apudetail/apudetail-1.0-SNAPSHOT.jar"
      route_key = "GET /apudetail/{id}"
    },
    update_apudetail = {
      handler   = "com.lambdas.handler.UpdateApuDetailHandler::handleRequest"
      jar_path  = "functions/apudetail/apudetail-1.0-SNAPSHOT.jar"
      route_key = "PUT /apudetail/{id}"
    },
    delete_apudetail = {
      handler   = "com.lambdas.handler.DeleteApuDetailHandler::handleRequest"
      jar_path  = "functions/apudetail/apudetail-1.0-SNAPSHOT.jar"
      route_key = "DELETE /apudetail/{id}"
    },

    # === Others Lambdas ===
    
  }
}
variable "common" {
  description = "Config del Lambda Layer de utilidades comunes"
  type = object({ zip_path = string })
  default = {
    zip_path = "layers/common/common.zip"
  }
}

# ----------------------------------------
# 2) AWS & VPC
# ----------------------------------------
variable "region" {
  description = "AWS region to deploy into"
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "az" {
  description = "Availability Zone"
  type        = string
  default     = "us-east-1a"
}

variable "public_subnets" {
  description = "CIDRs for public subnets"
  type        = list(string)
  default     = ["10.0.101.0/24"]
}

variable "private_subnets" {
  description = "CIDRs for private subnets"
  type        = list(string)
  default     = ["10.0.1.0/24"]
}

# ----------------------------------------
# 3) RDS DB
# ----------------------------------------
variable "db_username" {
  type    = string
  default = "innobra_user"
}


variable "db_name" {
  description = "Name of the PostgreSQL database"
  type        = string
  default     = "innobra_dev"
}

# ----------------------------------------
# 4) Lambda performance
# ----------------------------------------
variable "lambda_memory" {
  description = "Memory (MB) for each Lambda function"
  type        = number
  default     = 128
}

variable "lambda_timeout" {
  description = "Timeout (seconds) for each Lambda function"
  type        = number
  default     = 30
}

variable "lambda_bucket" {
  type = string
}

variable "db_host_value" {
  description = "RDS endpoint para entorno dev"
  type        = string
}