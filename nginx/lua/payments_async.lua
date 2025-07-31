local http = require("resty.http")

ngx.req.read_body()
local body = ngx.req.get_body_data()
--local headers = ngx.req.get_headers()

local function async_post(premature, body)
--local function async_post(premature, body)
    if premature then
        return
    end

    local httpc = http.new()
    local backends = {
        "http://backend-01:8080/payments",
        "http://backend-02:8080/payments"
    }

    --math.randomseed(os.time() + ngx.worker.pid())
    local backend = backends[math.random(#backends)]

    local res, err = httpc:request_uri(backend, {
        method = "POST",
        body = body,
        headers = {
            ["Content-Type"] = "application/json",
        },
    })

--     if not res then
--         ngx.log(ngx.ERR, "Failed to send async request to ", backend, ": ", err)
--     end
end

local ok, err = ngx.timer.at(0, async_post, body)

-- if not ok then
--     ngx.log(ngx.ERR, "Failed to create async timer: ", err)
-- end

return ngx.exit(202)