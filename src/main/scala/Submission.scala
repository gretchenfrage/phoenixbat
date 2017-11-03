trait Submission {

  def apply(test: String): Option[Seq[Any] => Any]

}