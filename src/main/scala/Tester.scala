trait Tester {

  def apply(submission: Submission): Map[String, String]

  def name: String

}