package org.cvogt.test.records

import org.scalautils.TypeCheckedTripleEquals._
import org.scalatest.FunSuite
import org.cvogt.compossible._
import org.cvogt.compossible.{Record => R}
import scala.language.postfixOps

//import RecordCompletion._
//import scala.language.reflectiveCalls

class RecordTest extends FunSuite {

  /*object Foo{  
    implicit def unpack2[R](record: Record[R]) = new{
      def name_= (v: Any): Record[R] = record
      def age_= (v: Any): Record[R] = record
      private [Foo] def name = ???//macro RecordWhiteboxMacros.lookupMacro2[K]
    }
  }
  import Foo.unpack2
  */
  test("basic") {
    val r: Record[{def name: String}]
      = Record.named(name="Chris")

    assert("Chris" === r.name)

    val r2 = r.copy(name = "Miguel")
    assert("Miguel" === r2.name)

    val r3 = r2 & Record.named(age=99)

    assert("Miguel" === r3.name)
    assert(99 === r3.age)

    {
      val person = Record.named(
        name = "Chris",
        age = 99,
        dob = new java.util.Date()
      )
      assert("Chris" === person.name)
      assert(99 === person.age)
      assert(person.dob === person.dob)
    };


    val person = Record(new{
      def name = "Chris"
      def age = 99
      def dob = new java.util.Date()
    })
    assert("Chris" === person.name)
    assert(99 === person.age)
    assert(person.dob === person.dob)
      
    val name = person.name
    val age = person.age
    val dob = person.dob
    
    (name: String, age: Int, dob: java.util.Date)


    val car = Record.named(owner="Chris",
                     model="Mercedes")

    {
      val merged = for{
        p <- List(person)
        c <- List(car) if c.owner == p.name
      } yield p & c

      merged.map{
        r =>
          assert(r.name === "Chris")
          assert(r.owner === "Chris")
          assert(r.model === "Mercedes")
          assert(r.age === 99)
          (r.name: String, r.age: Int, r.dob: java.util.Date, r.owner: String, r.model: String)
      }
    };

    {
      val merged = for{
        p <- List(person)
        c <- List(car) if c.owner == p.name
      } yield p(select name & age) &
              c(select.owner)

      merged.map{
        r =>
          assert(r.name === "Chris")
          assert(r.owner === "Chris")
          assertTypeError("""assert(r.model === "Mercedes")""")
          //assertTypeError("""r.dob: java.util.Date""")
          //assertTypeError("""r.model: String""")
          assert(r.age === 99)
          (r.name: String, r.age: Int, r.owner: String)
      }
    };

    {
      val personWithCar =
        Record.named(name="Chris",
                age =99,
                dob =new java.util.Date(),
                car =Record.named(owner="Chris",model="Mercedes"))

      assert("Chris" === personWithCar.car.owner)

      //new Bar.Foo[String]().name[Int](5)

      //val recordType = (RecordType age [Int] &
      //                             name[String] &
      //                             dob [java.util.Date] &)
      //type tpe = recordType.Type
      type Tpe = {
        def name: String
        def age: Int
        def dob: java.util.Date
      }
      def foo(record: Record[Tpe]) = record.name

      def foo2(record: Record[{
        def name: String
        def age: Int
        def dob: java.util.Date
      }]) = record.age

      assert("Chris" === foo(person))
      assert("Chris" === foo(personWithCar))

      //personWithCar.§.car(owner = "Miguel")
    };

    /*{
      case class Person(name: String, age: Int, dob: java.util.Date)
      val t = Record.tuple(person)
      (t: (String, Int, java.util.Date),())
      val p = Person.tupled(Record.tuple(person))
      val r = Record.fromCaseClass(p)
      assert(r.name === "Chris")
      assert(r.age === 99)
      (r.dob,())
    };*/

    {
      // Good Example Use Case
      case class Person(name: String, age: Int)
      case class PersonWithDob(name: String, age: Int, dob: java.util.Date)
      val p1 = Person("Chris",99)
      val r = Record.fromCaseClass(p1) &
              Record.named(dob=new java.util.Date)
      //val p2 = PersonWithDob.tupled(Record.tuple(r))
    };

    {
      val r = 
        Record.named(name="Chris") With
        Record.named(age=99) With
        Record.named(dob=new java.util.Date)
      
      val name = r.name
      val age = r.age
      val dob = r.dob

      r.age - 1

      assert("Chris" == r.name)
      assert(99 == r.age)

      assert("Chris" === r.name)
      assert(99 === r.age)

      val r2 = r.copy(name = "Miguel").copy(age = 98)
      assert("Miguel" === r2.name)
      assert(98 === r2.age)

//      val r3 = r(age = 98, name = Miguel)

//      val r3 = name = "Miguel").age = 98
      assert("Miguel" === r2.name)
      assert(98 === r2.age)

      //val r2 = r(update name & age) = ("")

      (name: String, age: Int, dob: java.util.Date)
    };

    { // contrast what you can and can't do with a map
      val m = Map[String, Any](
        "name" -> "Chris", "age" -> 99)

      assert("Chris" === m("name"))
      assert(99 === m("age"))

      // does not compile with a Map
      assertTypeError{
        """
          m("age") - 1
        """
      }
    }
/*
    {
      val name = person.extract("name").name
      val age = person.extract("age").age
      val dob = person.extract("dob").dob
      
      (name: String, age: Int, dob: java.util.Date)
    };
*/
    /*
    {
      val r = 
        (((Record.name = "Chris")
         .age = 99)
         .dob = new java.util.Date())
      
      val name = r.name
      val age = r.age
      val dob = r.dob
      
      (name: String, age: Int, dob: java.util.Date)
    };
    */
  }
}
