import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Link, useNavigate } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { Post } from "@/services/api";
import { AuthContext } from "@/context/AuthProvider";

export function ForgotForm({
  className,
  ...props
}: React.ComponentProps<"div">) {
  const [email, setEmail] = useState<string>("");
  const [timing, setTiming] = useState<number>(0);
  const [recoveryStatus, setRecoveryStatus] = useState<Boolean>(false);
  const url: string = "http://localhost:5432/recover";
  const AuthSettings = useContext(AuthContext);
  const navigate = useNavigate();
  function handleEmail(e: any) {
    setEmail(e.target.value ?? "");
  }

  async function get() {
    return {
      status: 400,
      ok: 1,
      message: "Invalid url",
      data: { user: {}, userToken: "azd" },
    };
  }
  async function login() {
    setRecoveryStatus(true);
    setTiming(5);
    let ret = { status: 400, ok: 0, message: "Invalid url" };
    try {
      if (url) {
        //   ret = await Post(url, { email: email });
        ret = await get();

        if (ret.ok === 1) {
          //recovery update
        } else {
          //error logging
          //later for push notification ->
          //display(ret.message)
          console.log("error");
        }
      }
    } catch (e: unknown) {
      ret = { status: 400, ok: 0, message: "Frontend error" };
      console.log(e.message + " " + e.stack);
    }
  }
  useEffect(() => {
    let id: any;
    let intid: any;
    if (recoveryStatus) {
      intid = setInterval(() => {
        setTiming((prev) => Math.max(prev - 1, 0));
      }, 1000);
      id = setTimeout(() => {
        setRecoveryStatus(false);
      }, 6000);
    }
    return () => {
      clearTimeout(id);
      clearInterval(intid);
    };
  }, [recoveryStatus]);
  return (
    <div className={cn("flex flex-col gap-6", className)} {...props}>
      <Card>
        <CardHeader className="text-center">
          <CardTitle className="text-xl">Recover Password</CardTitle>
          <CardDescription></CardDescription>
        </CardHeader>
        <CardContent>
          <form>
            <div className="grid gap-6">
              <div className="flex flex-col gap-4"></div>
              <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                <span className="bg-card text-muted-foreground relative z-10 px-2">
                  Recover with
                </span>
              </div>
              <div className="grid gap-6">
                <div className="grid gap-3">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    placeholder="m@example.com"
                    required
                    onChange={handleEmail}
                  />
                </div>
                <Button
                  disabled={recoveryStatus}
                  onClick={login}
                  type="button"
                  className="w-full"
                >
                  Recover
                  {recoveryStatus && ` (${timing})`}
                </Button>
              </div>
              <div className="text-center text-sm">
                Don&apos;t have an account?
                <Link to="/signup"> Sign up</Link>
              </div>
              <div className="text-center text-sm">
                Account Recovered ?<Link to="/login"> Login </Link>
              </div>
            </div>
          </form>
        </CardContent>
      </Card>
      <div className="text-muted-foreground *:[a]:hover:text-primary text-center text-xs text-balance *:[a]:underline *:[a]:underline-offset-4">
        By clicking continue, you agree to our <a href="#">Terms of Service</a>{" "}
        and <a href="#">Privacy Policy</a>.
      </div>
    </div>
  );
}
