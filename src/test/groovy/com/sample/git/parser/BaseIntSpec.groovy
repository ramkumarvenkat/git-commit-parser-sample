package com.sample.git.parser


import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ActiveProfiles(["test"])
@ContextConfiguration(loader = SpringBootContextLoader)
class BaseIntSpec extends BaseSpec {
}
