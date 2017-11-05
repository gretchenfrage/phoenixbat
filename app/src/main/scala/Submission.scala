trait Submission {

  def apply(test: String, args: Seq[Any], expected: Any): TestResult

}