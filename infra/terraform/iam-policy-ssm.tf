resource "aws_iam_policy" "ms_production_ssm" {
  name = "ms-production-ssm-policy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "ssm:GetParameter",
        "ssm:GetParameters"
      ]
      Resource = "arn:aws:ssm:${var.aws_region}:500782030170:parameter/*"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_attach" {
  role       = aws_iam_role.ms_production_irsa.name
  policy_arn = aws_iam_policy.ms_production_ssm.arn
}
