resource "aws_iam_policy" "ms_production_sqs" {
  name = "ms-production-sqs-policy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "sqs:ReceiveMessage",
        "sqs:DeleteMessage",
        "sqs:GetQueueAttributes"
      ]
      Resource = [
        data.terraform_remote_state.sqs.outputs.sqs_queue_arns["production-callback-queue"],
        data.terraform_remote_state.sqs.outputs.sqs_queue_arns["production-queue"]
      ]
    }]
  })
}

resource "aws_iam_role_policy_attachment" "sqs_attach" {
  role       = aws_iam_role.ms_production_irsa.name
  policy_arn = aws_iam_policy.ms_production_sqs.arn
}
