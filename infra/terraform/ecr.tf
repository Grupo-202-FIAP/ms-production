resource "aws_ecr_repository" "ms_production" {
  name                 = "ms-production"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}
