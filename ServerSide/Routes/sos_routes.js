const express = require("express");
const router = express.Router();

const Sos = require('../Controllers/soss');
const authenticate = require('../Common/auth_middleware');

router.get('/', authenticate, Sos.getSoss);
router.post('/', authenticate, Sos.addSos);

module.exports = router