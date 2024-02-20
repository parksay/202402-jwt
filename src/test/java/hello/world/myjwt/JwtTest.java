package hello.world.myjwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtTest {

    // https://jwt.io/#debugger-io

    private SecretKey key = Jwts.SIG.HS256.key().build();

    @Test
    public void hello() {
        // https://velog.io/@suhyun_zip/JWT-%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
        // https://gong-check.github.io/dev-blog/BE/%EC%96%B4%EC%8D%B8%EC%98%A4/jwt_impl/jwt_impl/
        // https://www.youtube.com/watch?v=1QiOXWEbqYQ
        System.out.println("hello world!!! = ");
    }

    @Test
    public void issueJws() {
        // 참고로 JWS와 JWE는 JWT의 종류에 속하며
        // 클레임 셋이 암호화 된 JWT는 JWE(Java Web Encryption),
        // 클레임 셋이 암호화 되지 않은 JWT는 JWS(Java Web Segniture)가 됩니다.
        // 대부분 JWT라고 말하면 JWS를 칭하는 편입니다.
        // https://targetcoders.com/jjwt-%EC%82%AC%EC%9A%A9-%EB%B0%A9%EB%B2%95/


        // 헤더만 있고 페이로드랑 시그니처는 없음
        String jwsNoPayloadNoSignature  = Jwts.builder().compact();
        System.out.println("jwsNoPayloadNoSignature = " + jwsNoPayloadNoSignature);
        // 헤더랑 시그니처만 있고 페이로드가 없음
        String jwsNoPayload = Jwts.builder().signWith(this.key).compact();
        System.out.println("jwsNoPayload = " + jwsNoPayload);
        // 헤더랑 페이로드만 있고 시그니처가 없음
        String jwsNoSignature = Jwts.builder().claim("myKey", "myVal").compact();
        System.out.println("jwsNoSignature = " + jwsNoSignature);
        // 헤더랑 페이로드랑 시그니처 다 있음
        String jws = Jwts.builder().claim("myKey", "myVal").signWith(this.key).compact();
        System.out.println("jws = " + jws);
        // 출력 결과 보면 a.b.c 형태이고 페이로드나 시그니처가 비어 있더라도 .은 그대로 찍힘.
        // JwtBuilder 객체 하나 만들어서 subject 도 set 하고 issuer 도 set 하고 payload 도 set 하고 하다가
        // 마지막에 compact() 가 결국 문자열로 바꿔주는 흐름
        this.verifyTest(jws);
        //


    }

    private void verifyTest(String jws) {
        assert Jwts.parser().verifyWith(this.key).build().parseSignedClaims(jws).getPayload().get("myKey").equals("myVal");
        Assertions.assertTrue(
                Jwts.parser()
                        .verifyWith(this.key)
                        .build()
                        .parseSignedClaims(jws)
                        .getPayload()
                        .get("myKey")
                        .equals("myVal"));
    }

    @Test
    public void verifyFailTest() {
        // key 를 맞지 않는 걸 들고서 signedClaims 를 parse  하려고 시도하면 SignatureException 예외 발생함
        // key 는 실행할 당시에 랜덤으로 생성하기 때문에 이전에 돌렸을 때 만든 jws 로 돌리면 현재 key 랑 만들 당시 key 가 달라서 실패함.
        String jws = "eyJhbGciOiJIUzI1NiJ9.eyJteUtleSI6Im15VmFsIn0.brAFL7RWvEO0ki2dckQnHiJhxpsQDyGo_-c4wbrAkhQ";
        Assertions.assertThrows(SignatureException.class, ()->{
            assert Jwts.parser().verifyWith(this.key).build().parseSignedClaims(jws).getPayload().get("myKey").equals("myVal");
        });
        Assertions.assertThrows(SignatureException.class, ()->{
            Jwts.parser()
                    .verifyWith(this.key)
                    .build()
                    .parseSignedClaims(jws)
                    .getPayload()
                    .get("myKey")
                    .equals("myVal");
        });

    }

    @Test
    public void createJwtTest() {
        // 1.Use the Jwts.builder() method to create a JwtBuilder instance.
        // 2.Optionally set any header parameters as desired.
        // 3.Call builder methods to set the payload content or claims.
        // 4.Optionally call signWith or encryptWith methods if you want to digitally sign or encrypt the JWT.
        // 5.Call the compact() method to produce the resulting compact JWT string.

        Key signingKey = this.key;
        byte[] myContent = "this is my content".getBytes(StandardCharsets.UTF_8);
        Map<String, String> myMap = new HashMap<String, String>();
        URI myUri = URI.create("this-is-x509uri");
        myMap.put("map1", "val1");
        myMap.put("map2", "val2");
        String jwt = Jwts.builder()                     // (1)
                .header()                               // (2) optional
                    .keyId("myKeyId")                      // set keyId
                    .x509Url(myUri)        // set x509uri
                    .add("add1", "val1")                // optional payload
                    .add("add2", "val2")                // optional payload
                    .add(myMap)                               // optional payload
                    .and()                                    // go back to the JwtBuilder
                .subject("John")                      // (3) optional
                .claim("claim1", "data1")                   // JSON Claims
                .claim("claim2", "data2")                   // JSON Claims
                .claims(myMap)                              // JSON Claims
//                .content(myContent, "text/plain")                //   or  any byte[] content, with media type
                .signWith(signingKey)                    // (4) if signing, or
                //.encryptWith(key, keyAlg, encryptionAlg)  //     if encrypting
                .compact();                              // (5)

        System.out.println("jwt = " + jwt);
    }

    @Test
    public void createHeaderTest() {
        URI myUri = URI.create("this-is-x509uri");
        Map<String, String> myMap = new HashMap<String, String>();
        myMap.put("map1", "val1");
        myMap.put("map2", "val2");

        // 헤더만 따로 떼어서 만들고 싶을 떄는 이렇게 header() 메소드만 바로 부르면 됨
        // 마지막에는 and() 가 아니라 build() 로 끝내는 점 주의
        Header header = Jwts.header()
                .keyId("aKeyId")
                .x509Url(myUri)
                .add("someName", "here")
                .add(myMap)
                // ... etc ...
                .build();  // <---- not 'and()'
    }

    @Test
    public void createContentPayloadTest() {
        // A JWT payload can be anything at all - anything that can be represented as a byte array, such as text, images, documents, and more.
        // But since a JWT header is always JSON, it makes sense that the payload could also be JSON, especially for representing identity claims.
        // JWT 페이로드는 바이트 배열로 표현될 수만 있으면 뭐든 넣을 수 있음.
        // 텍스트, 이미지, 문서 등 모두 가능.
        // 그러나 JWT 헤더는 항상 JSON 이기 때문에 위해 페이로드가 JSON일 수도 있는 게 자연스러움.
        // content() if you would like the payload to be arbitrary byte array content, or
        // claims() (and supporting helper methods) if you would like the payload to be a JSON Claims Object.
        // payload 를 byte array 로 넣고 싶으면 content() 메소드
        // JSON 형태로 넣고 싶으면 claims() 또는 claim()
        byte[] content = "Hello World".getBytes(StandardCharsets.UTF_8);
        String jwt1 = Jwts.builder()
                .content(content, "text/plain") // <---
                // ... etc ...
                .compact();
        System.out.println("jwt1 = " + jwt1);

    }

    @Test
    public void createClaimPayloadTest() {
        // issuer(): sets the iss (Issuer) Claim
        // subject(): sets the sub (Subject) Claim
        // audience(): sets the aud (Audience) Claim
        // expiration(): sets the exp (Expiration Time) Claim
        // notBefore(): sets the nbf (Not Before) Claim
        // issuedAt(): sets the iat (Issued At) Claim
        // id(): sets the jti (JWT ID) Claim
        // 위에는 이미 정해놓은 형식으로 claim 넣기
        // 그거 말고 그냥 내가 만든 custom claim 넣으려면 claim() 또는 claims()
        Date issuedDate = new Date();
        Date expiration = new Date(issuedDate.toInstant().toEpochMilli() + 60L*60L);
        Date notBefore = new Date(expiration.toInstant().toEpochMilli() + 60L*60L);
        Map<String, String> myMap = new HashMap<String, String>();
        myMap.put("map1", "val1");
        myMap.put("map2", "val2");

        String jws = Jwts.builder()
                .issuer("myIssuer")
                .subject("mySub")
                .audience().add("myAud1").add("myAud2").and()
                .expiration(expiration) //a java.util.Date
                .notBefore(notBefore) //a java.util.Date
                .issuedAt(new Date()) // for example, now
                .id(UUID.randomUUID().toString()) //just an example id
                .claim("claim1", "val1")    // custom claim key, val
                .claims(myMap)                  // custom claim map
                .compact();
        System.out.println("jws = " + jws);
    }

    @Test
    public void parserBuilderTest() {
        Jwt<?,?> jwt;
        Locator<Key> keyLocator = null;
        CharSequence compact = null;
        try {
            jwt = Jwts.parser()     // (1)
                    .keyLocator(keyLocator) // (2) dynamically locate signing or encryption keys
                    //.verifyWith(key)      //     or a constant key used to verify all signed JWTs
                    //.decryptWith(key)     //     or a constant key used to decrypt all encrypted JWTs
                    .build()                // (3)
                    .parse(compact);        // (4) or parseSignedClaims, parseEncryptedClaims, parseSignedContent, etc
            // we can safely trust the JWT
        } catch (JwtException ex) {   // (5)
            // we *cannot* use the JWT as intended by its creator
        }
    }

    @Test
    public void constantParsingTest() {
        String jwsString = "eyJhbGciOiJIUzI1NiJ9.eyJteUtleSI6Im15VmFsIn0.brAFL7RWvEO0ki2dckQnHiJhxpsQDyGo_-c4wbrAkhQ";;
        String jweString = "eyJhbGciOiJIUzI1NiJ9.eyJteUtleSI6Im15VmFsIn0.brAFL7RWvEO0ki2dckQnHiJhxpsQDyGo_-c4wbrAkhQ";;
        SecretKey secretKey = null;
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        // constant parsing key
        // >> If parsing a JWS and the JWS was signed with a SecretKey, the same SecretKey should be specified on the JwtParserBuilder. For example:
        // 'SecretKey'로 서명된 JWS 구문을 분석하는 경우, 해당 키와 동일한 'SecretKey'를 'JwtParserBuilder'에 지정해야 합니다. 예를 들어 다음과 같습니다:
        Jwts.parser()
                .verifyWith(secretKey) // <----
                .build()
                .parseSignedClaims(jwsString);
        // >> If parsing a JWS and the JWS was signed with a PrivateKey, that key’s corresponding PublicKey (not the PrivateKey) should be specified on the JwtParserBuilder. For example
        // 'PrivateKey'로 서명된 JWS 구문을 분석하는 경우, 해당 키의 'PublicKey'('PrivateKey'가 아님)를 'JwtParserBuilder'에 지정해야 합니다. 예를 들어 다음과 같습니다:
        Jwts.parser()
                .verifyWith(publicKey) // <---- publicKey, not privateKey
                .build()
                .parseSignedClaims(jwsString);
        // >> If parsing a JWE and the JWE was encrypted with direct encryption using a SecretKey, the same SecretKey should be specified on the JwtParserBuilder. For example:
        // 'SecretKey'를 사용하여 직접 암호화된 JWE를 구문 분석하는 경우, 해당 키와 동일한 'SecretKey'를 'JwtParserBuilder'에 지정해야 합니다. 예를 들어 다음과 같습니다:
        Jwts.parser()
                .decryptWith(secretKey) // <---- or a Password from Keys.password(charArray)
                .build()
                .parseEncryptedClaims(jweString);
        // >> If parsing a JWE and the JWE was encrypted with a key algorithm using with a PublicKey, that key’s corresponding PrivateKey (not the PublicKey) should be specified on the JwtParserBuilder. For example:
        // 'PublicKey'를 사용한 키 알고리즘으로 암호화환 JWE를 구문 분석하는 경우, 해당 키의 'PrivateKey'('PublicKey'가 아님)를 'JwtParserBuilder'에 지정해야 합니다. 예를 들어 다음과 같습니다:
        Jwts.parser()
                .decryptWith(privateKey) // <---- privateKey, not publicKey
                .build()
                .parseEncryptedClaims(jweString);
    }


    @Test
    public void parseDetachedPayloadTest() {
        // Detached Payload Example
        // >> This example shows creating and parsing a compact JWS using an unencoded payload that is detached, i.e. where the payload is not embedded in the compact JWS string at all.
        //We need to do three things during creation:
        //Specify the JWS signing key; it’s a JWS and still needs to be signed.
        //Specify the raw payload bytes via the JwtBuilder's content method.
        //Indicate that the payload should not be Base64Url-encoded using the JwtBuilder's encodePayload(false) method.
        // >> 아래 예제에서는 인코딩되지 않은 페이로드를 detach 형태로 보낼 때, 즉 페이로드가 컴팩트 JWS 문자열에 전혀 들어가지 않은 상태에서 컴팩트 JWS를 생성하고 구문을 분석하는 방법을 보여줍니다.
        //우리는 만드는 동안 다음 세 가지를 수행해야 합니다:
        //JWS 서명 키를 지정하십시오. JWS이며 서명이 필요합니다.
        //JwtBuilder의 콘텐츠 방식을 통해 원시 페이로드 바이트를 지정합니다.
        //JwtBuilder의 encodePayload(false) 메서드를 사용하여 페이로드를 Base64Url로 인코딩하지 않아야 함을 나타냅니다.
        SecretKey testKey = Jwts.SIG.HS512.key().build();
        String message = "Hello World. It's a Beautiful Day!";
        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        String jws = Jwts.builder()
                .signWith(testKey)                      // #1
                .content(content)                     // #2
                .encodePayload(false)                 // #3
                .compact();

        //
        Jws<byte[]> parsed = Jwts.parser()
                .verifyWith(testKey) // 1
                .build()
                .parseSignedContent(jws, content);             // 2
        System.out.println("parsed = " + parsed);
        Assertions.assertEquals(content, parsed.getPayload());
    }

    @Test
    public void parseNonDetachedPayloadTest() {
        // Non-Detached Payload Example
        // This example shows creating and parsing a compact JWS with what the RFC calls a 'non-detached' unencoded payload, i.e. a raw string directly embedded as the payload in the compact JWS string.
        //We need to do three things during creation:
        //Specify the JWS signing key; it’s a JWS and still needs to be signed.
        //Specify the raw payload string via the JwtBuilder's content method. Per the RFC, the payload string MUST NOT contain any period (.) characters.
        //Indicate that the payload should not be Base64Url-encoded using the JwtBuilder's encodePayload(false) method.
        // 아래 예제에서는 인코딩되지 않은 페이로드를 '분리되지 않은(RFC가 'non-detached' 라고 부르는 형태)'형태로 보낼 때, 즉 컴팩트 JWS 문자열에 페이로드로 직접 내장된 원시 문자열을 사용하여 컴팩트 JWS를 생성하고 구문 분석하는 것을 보여줍니다.
        //우리는 창작하는 동안 다음 세 가지를 수행해야 합니다:
        //JWS 서명 키를 지정하십시오. JWS이며 서명이 필요합니다.
        //JwtBuilder의 콘텐츠 메서드를 통해 원시 페이로드 문자열을 지정합니다. RFC에 따라 페이로드 문자열에는 마침표(.) 문자가 없어야 합니다.
        //JwtBuilder의 encodePayload(false) 메서드를 사용하여 페이로드를 Base64Url로 인코딩하지 않아야 함을 나타냅니다.

        // create a test key for this example:
        SecretKey testKey = Jwts.SIG.HS512.key().build();
        String claimsString = "{\"sub\":\"joe\",\"iss\":\"me\"}";
        String jws = Jwts.builder()
                .signWith(testKey)                       // #1
                .content(claimsString)                // #2
                .encodePayload(false)                 // #3
                .compact();

        //
        Jws<Claims> parsed = Jwts.parser()
                .verifyWith(testKey)                            // 1
                .critical().add("b64").and()                   // 2
                .build()
                .parseSignedClaims(jws);
        Assertions.assertEquals("joe", parsed.getPayload().getSubject());
        Assertions.assertEquals("me", parsed.getPayload().getIssuer());
        System.out.println("parsed1 = " + parsed);

        //
        parsed = Jwts.parser().verifyWith(testKey)
                .build()
                .parseSignedClaims(jws, claimsString.getBytes(StandardCharsets.UTF_8)); // <---
        System.out.println("parsed2 = " + parsed);
    }


}
