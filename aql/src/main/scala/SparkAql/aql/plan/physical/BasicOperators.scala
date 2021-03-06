package SparkAql.aql.plan.physical

import SparkAql.aql.execution.{TextRegexMatcher, TextTokenizer}
import SparkAql.aql.model.{Span, Tuple}
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

case class PhysicalDictView(dictName: String, output: Seq[String]) extends LeafSparkNode{

  override def doExecute(): RDD[Tuple] = {
    val input = aqlContext.getDocument
    val dict = aqlContext.lookforDict(dictName).toSet
    val regex = "[\\s\\p{Punct}]+"
    val tokens = TextTokenizer(input,regex)
    var matchList = ArrayBuffer[Tuple]()

    for(elem <- tokens){
      if(dict.contains(elem.text)){
        matchList += new Tuple(new Span(elem.start,elem.end)::Nil)
      }
    }

    sparkContext.parallelize(matchList)
  }

}

case class PhysicalRegexView(regex: String, output: Seq[String]) extends LeafSparkNode{

  override def doExecute(): RDD[Tuple] = {
    val input = aqlContext.getDocument
    val tokens = TextRegexMatcher(input,regex)
    var matchList = ArrayBuffer[Tuple]()

    for(elem <- tokens){
        matchList += new Tuple(new Span(elem.start,elem.end)::Nil)
    }

    sparkContext.parallelize(matchList)
  }

}
