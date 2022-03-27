const express = require("express");
const router = express.Router();

const Auth = require("../Controllers/auth");

/**
 * @swagger
 * tags:
 *   name: Auth API
 *   description: The Authentication API
 */

/**
 * @swagger
 * components:
 *   securitySchemes:
 *       bearerAuth:
 *           type: http
 *           scheme: bearer
 *           bearerFormat: JWT
 */

/**
 * @swagger
 * components:
 *   schemas:
 *       User Authentication:
 *           type: object
 *           required:
 *               - email
 *               - password
 *           properties:
 *               email:
 *                   type: string
 *                   description: The user email
 *               password:
 *                   type: string
 *                   description: The user password
 *           example:
 *               email: 'bob@gmail.com'
 *               password: '123456'
 */

/**
 * @swagger
 * components:
 *   schemas:
 *       Tokens:
 *           type: object
 *           required:
 *               - accessToken
 *               - refreshToken
 *           properties:
 *               accessToken:
 *                   type: string
 *                   description: The JWT access token
 *               refreshToken:
 *                   type: string
 *                   description: The JWT refresh token
 *           example:
 *               accessToken: '123cd123x1xx1'
 *               refreshToken: '134r2134cr1x3c'
 */

/**
 * @swagger
 * /auth/register:
 *   post:
 *       summary: registers a new user
 *       tags: [Auth API]
 *       requestBody:
 *           required: true
 *           content:
 *               application/json:
 *                   schema:
 *                       $ref: '#/components/schemas/User'
 *       responses:
 *           200:
 *               description: The new user
 *               content:
 *                   application/json:
 *                       schema:
 *                           $ref: '#/components/schemas/User'
 */

router.post("/register", Auth.register);

/**
 * @swagger
 * /auth/login:
 *   post:
 *       summary: registers a new user
 *       tags: [Auth API]
 *       requestBody:
 *           required: true
 *           content:
 *               application/json:
 *                   schema:
 *                       $ref: '#/components/schemas/User'
 *       responses:
 *           200:
 *               description: The acess & refresh tokens
 *               content:
 *                   application/json:
 *                       schema:
 *                           $ref: '#/components/schemas/Tokens'
 */

router.post("/login", Auth.login);

/**
 * @swagger
 * /auth/logout:
 *   get:
 *       summary: logout a user
 *       tags: [Auth API]
 *       description: need to provide the refresh token in the auth header
 *       security:
 *           - bearerAuth: []
 *       responses:
 *           200:
 *               description: logout completed successfully
 */
router.get("/logout", Auth.logout);

/**
 * @swagger
 * /auth/refreshToken:
 *   get:
 *       summary: get a new access token using the refresh token
 *       tags: [Auth API]
 *       description: need to provide the refresh token in the auth header
 *       security:
 *           - bearerAuth: []
 *       responses:
 *           200:
 *               description: The acess & refresh tokens
 *               content:
 *                   application/json:
 *                       schema:
 *                           $ref: '#/components/schemas/Tokens'
 */

// router.get("/refreshToken", Auth.refreshToken);

router.post("/resendEmail", Auth.resendEmail);
router.post("/afterVerify", Auth.verifiedUser);
router.get("/forgotPassword", Auth.forgotPassword);
router.get("/findByEmail", Auth.findUserByEmail);
router.get("/findById", Auth.findUserById);

module.exports = router;
