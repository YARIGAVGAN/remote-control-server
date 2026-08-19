# remote-control-server

Linux server for a local remote-control system. The server accepts REST/JSON commands from Android and Windows clients.

This version contains the base Spring Boot server, API key authorization, health endpoint, mouse control, keyboard control through `xdotool`, and Linux service management for `x11vnc`, `ssh`, and `lircd`.

## Requirements

- Linux 
- Java 17
- Gradle wrapper from this repository

Install Java and the Linux tools used by this project:

```bash
sudo apt update
sudo apt install openjdk-17-jdk xdotool openssh-server x11vnc lirc
```

For mouse control only:

```bash
sudo apt install xdotool
```

`xdotool` works normally under X11. It does not work reliably under Wayland, so Linux Mint XFCE should be running an X11 session.

## x11vnc systemd Service

Create `/etc/systemd/system/x11vnc.service`:

```ini
[Unit]
Description=x11vnc remote desktop server
After=display-manager.service network.target

[Service]
Type=simple
User=remotecontrol
Environment=DISPLAY=:0
Environment=XAUTHORITY=/home/remotecontrol/.Xauthority
ExecStart=/usr/bin/x11vnc -display :0 -auth /home/remotecontrol/.Xauthority -forever -shared -rfbport 5900
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Reload systemd and enable the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable x11vnc
```

Check service status:

```bash
systemctl status x11vnc
systemctl status ssh
```

If Spring Boot runs as a regular user, `systemctl start/stop/restart` may require `sudo`. One option is a narrow sudoers rule for the application user:

```sudoers
remotecontrol ALL=NOPASSWD: /bin/systemctl start x11vnc, /bin/systemctl stop x11vnc, /bin/systemctl restart x11vnc, /bin/systemctl is-active x11vnc
```

Apply sudoers changes with `sudo visudo`.

## Run

```bash
./gradlew bootRun
```

On Windows during development:

```powershell
.\gradlew.bat bootRun
```

The server starts on port `8080`.

## Configuration

Default configuration is in `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

app:
  api-key: CHANGE_ME
  vnc:
    service-name: x11vnc
  ir:
    default-remote: Samsung
```

## Check With curl

Health endpoint does not require an API key:

```bash
curl http://localhost:8080/api/health
```

Expected response:

```json
{
  "status": "ok",
  "server": "remote-control-server",
  "timestamp": "2026-06-16T15:00:00Z"
}
```

All other `/api/**` endpoints must include the API key header:

```bash
curl -H "X-API-Key: CHANGE_ME" http://localhost:8080/api/some-endpoint
```

Mouse left click:

```bash
curl -X POST http://localhost:8080/api/mouse/click \
  -H "Content-Type: application/json" \
  -H "X-API-Key: CHANGE_ME" \
  -d '{"button":"left"}'
```

Mouse left button hold:

```bash
curl -X POST http://localhost:8080/api/mouse/down \
  -H "Content-Type: application/json" \
  -H "X-API-Key: CHANGE_ME" \
  -d '{"button":"left"}'
```

Mouse left button release:

```bash
curl -X POST http://localhost:8080/api/mouse/up \
  -H "Content-Type: application/json" \
  -H "X-API-Key: CHANGE_ME" \
  -d '{"button":"left"}'
```

Mouse move:

```bash
curl -X POST http://localhost:8080/api/mouse/move \
  -H "Content-Type: application/json" \
  -H "X-API-Key: CHANGE_ME" \
  -d '{"dx":10,"dy":-5}'
```

Mouse scroll down:

```bash
curl -X POST http://localhost:8080/api/mouse/scroll \
  -H "Content-Type: application/json" \
  -H "X-API-Key: CHANGE_ME" \
  -d '{"amount":-3}'
```

Type text:

```bash
curl -X POST http://localhost:8080/api/keyboard/text \
  -H "Content-Type: application/json" \
  -H "X-API-Key: CHANGE_ME" \
  -d '{"text":"hello"}'
```

Press a key:

```bash
curl -X POST http://localhost:8080/api/keyboard/key \
  -H "Content-Type: application/json" \
  -H "X-API-Key: CHANGE_ME" \
  -d '{"key":"ENTER"}'
```

Press a shortcut:

```bash
curl -X POST http://localhost:8080/api/keyboard/shortcut \
  -H "Content-Type: application/json" \
  -H "X-API-Key: CHANGE_ME" \
  -d '{"keys":["CTRL","ALT","T"]}'
```

System volume:

```bash
curl -X POST http://localhost:8080/api/system/volume/up \
  -H "X-API-Key: CHANGE_ME"

curl -X POST http://localhost:8080/api/system/volume/down \
  -H "X-API-Key: CHANGE_ME"
```

Volume changes use a 5% step and try PipeWire `wpctl`, PulseAudio `pactl`, ALSA `amixer`,
and multimedia key fallbacks.

Suspend Linux:

```bash
curl -X POST http://localhost:8080/api/system/sleep \
  -H "X-API-Key: CHANGE_ME"
```

`/api/system/sleep` tries `systemctl suspend`, `systemctl -i suspend`,
`loginctl suspend`, `sudo -S /bin/systemctl suspend`, and `sudo -S systemctl suspend`.
If every command fails, the endpoint returns HTTP 500 with the collected command errors.
If the server runs as a systemd service and
PolicyKit denies suspend, add a narrow sudoers rule for the application user:

```text
remotecontrol ALL=NOPASSWD: /bin/systemctl suspend
```

Start VNC:

```bash
curl -X POST http://localhost:8080/api/services/vnc/start \
  -H "X-API-Key: CHANGE_ME"
```

Stop VNC:

```bash
curl -X POST http://localhost:8080/api/services/vnc/stop \
  -H "X-API-Key: CHANGE_ME"
```

Check VNC status:

```bash
curl http://localhost:8080/api/services/vnc/status \
  -H "X-API-Key: CHANGE_ME"
```

Check SSH status:

```bash
curl http://localhost:8080/api/services/ssh/status \
  -H "X-API-Key: CHANGE_ME"
```

Check LIRC status:

```bash
curl http://localhost:8080/api/services/lirc/status \
  -H "X-API-Key: CHANGE_ME"
```

Restart LIRC:

```bash
curl -X POST http://localhost:8080/api/services/lirc/restart \
  -H "X-API-Key: CHANGE_ME"
```

If the key is missing or invalid, the server returns:

```json
{
  "success": false,
  "message": "Unauthorized"
}
```
