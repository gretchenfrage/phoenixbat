trait Submission {

  def apply(test: String, args: Seq[Any], acceptance: Any => Boolean): TestResult

}